import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class DbCodeGenerator {
    private static final String JDBC_URL = "jdbc:mysql://127.0.0.1:3306/your_database"
            + "?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai"
            + "&allowPublicKeyRetrieval=true&useSSL=false&useInformationSchema=true";
    private static final String JDBC_USERNAME = "root";
    private static final String JDBC_PASSWORD = "change_me";
    private static final String JDBC_DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";
    private static final Path JDBC_DRIVER_JAR = Path.of("");

    private static final Path PROJECT_ROOT = Path.of(".");
    private static final String DB_MODULE_NAME = "career-db";
    private static final String BASE_PACKAGE = "bigtreecloud.career.db";
    private static final String SOURCE_ROOT = DB_MODULE_NAME + "/src/main/java";
    private static final String TABLE_NAME_PATTERN = "%";
    private static final Set<String> TABLE_NAMES = Set.of();
    private static final boolean OVERWRITE_EXISTING_FILES = false;

    private static final Set<String> INSERT_FILL_COLUMNS = Set.of("created_at", "created_by");
    private static final Set<String> INSERT_UPDATE_FILL_COLUMNS = Set.of("updated_at", "updated_by");

    private DbCodeGenerator() {
    }

    public static void main(String[] args) throws Exception {
        registerJdbcDriver();
        GenerateRequest request = configuredRequest();
        List<GeneratedFile> generatedFiles = generateFromDatabase(request);
        printSummary(generatedFiles);
    }

    public static List<GeneratedFile> generateFromDatabase(GenerateRequest request) throws SQLException, IOException {
        Objects.requireNonNull(request, "request must not be null");
        List<GeneratedFile> generatedFiles = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(
                request.jdbcUrl(), request.username(), request.password())) {
            DatabaseMetaData metaData = connection.getMetaData();
            List<TableMeta> tables = loadTables(metaData, request);
            for (TableMeta table : tables) {
                generatedFiles.add(writeFile(request, entityPath(request, table), renderEntity(table)));
                generatedFiles.add(writeFile(request, mapperPath(request, table), renderMapper(table)));
                generatedFiles.add(writeFile(request, daoPath(request, table), renderDao(table)));
                generatedFiles.add(writeFile(request, daoImplPath(request, table), renderDaoImpl(table)));
            }
        }
        return generatedFiles;
    }

    private static GenerateRequest configuredRequest() {
        return new GenerateRequest(JDBC_URL, JDBC_USERNAME, JDBC_PASSWORD, PROJECT_ROOT.toAbsolutePath().normalize(),
                null, null, TABLE_NAME_PATTERN, TABLE_NAMES, OVERWRITE_EXISTING_FILES);
    }

    private static void registerJdbcDriver() throws Exception {
        Path driverJar = resolveJdbcDriverJar();
        if (driverJar == null) {
            try {
                Class.forName(JDBC_DRIVER_CLASS);
            } catch (ClassNotFoundException ex) {
                throw new IllegalStateException("Cannot find MySQL JDBC driver. Set JDBC_DRIVER_JAR in this file "
                        + "or make sure mysql-connector-j exists in the local Maven repository.", ex);
            }
            return;
        }
        URLClassLoader classLoader = new URLClassLoader(new URL[]{driverJar.toUri().toURL()},
                DbCodeGenerator.class.getClassLoader());
        Driver driver = (Driver) Class.forName(JDBC_DRIVER_CLASS, true, classLoader)
                .getDeclaredConstructor()
                .newInstance();
        DriverManager.registerDriver(new DriverShim(driver));
    }

    private static Path resolveJdbcDriverJar() throws IOException {
        if (Files.isRegularFile(JDBC_DRIVER_JAR)) {
            return JDBC_DRIVER_JAR.toAbsolutePath().normalize();
        }
        Path mavenDriverDir = Path.of(System.getProperty("user.home"), ".m2", "repository",
                "com", "mysql", "mysql-connector-j");
        if (!Files.isDirectory(mavenDriverDir)) {
            return null;
        }
        try (Stream<Path> paths = Files.walk(mavenDriverDir)) {
            return paths.filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().matches("mysql-connector-j-[\\d.]+\\.jar"))
                    .sorted((left, right) -> right.toString().compareTo(left.toString()))
                    .findFirst()
                    .orElse(null);
        }
    }

    private static List<TableMeta> loadTables(DatabaseMetaData metaData, GenerateRequest request) throws SQLException {
        List<TableMeta> tables = new ArrayList<>();
        try (ResultSet tableRows = metaData.getTables(
                request.catalog(), request.schemaPattern(), request.tableNamePattern(), new String[]{"TABLE"})) {
            while (tableRows.next()) {
                String tableName = tableRows.getString("TABLE_NAME");
                if (!request.tableNames().isEmpty() && !request.tableNames().contains(tableName)) {
                    continue;
                }
                String remarks = tableRows.getString("REMARKS");
                tables.add(new TableMeta(tableName, remarks, loadColumns(metaData, request, tableName),
                        loadPrimaryKeys(metaData, request, tableName)));
            }
        }
        return tables;
    }

    private static List<ColumnMeta> loadColumns(DatabaseMetaData metaData, GenerateRequest request, String tableName)
            throws SQLException {
        Map<String, ColumnMeta> columns = new LinkedHashMap<>();
        try (ResultSet columnRows = metaData.getColumns(
                request.catalog(), request.schemaPattern(), tableName, "%")) {
            while (columnRows.next()) {
                String columnName = columnRows.getString("COLUMN_NAME");
                int dataType = columnRows.getInt("DATA_TYPE");
                String typeName = columnRows.getString("TYPE_NAME");
                String remarks = columnRows.getString("REMARKS");
                int nullable = columnRows.getInt("NULLABLE");
                columns.put(columnName, new ColumnMeta(columnName, toFieldName(columnName), dataType, typeName,
                        remarks, nullable == DatabaseMetaData.columnNullable));
            }
        }
        return new ArrayList<>(columns.values());
    }

    private static Set<String> loadPrimaryKeys(DatabaseMetaData metaData, GenerateRequest request, String tableName)
            throws SQLException {
        Set<String> primaryKeys = new LinkedHashSet<>();
        try (ResultSet keyRows = metaData.getPrimaryKeys(request.catalog(), request.schemaPattern(), tableName)) {
            while (keyRows.next()) {
                primaryKeys.add(keyRows.getString("COLUMN_NAME"));
            }
        }
        return primaryKeys;
    }

    private static GeneratedFile writeFile(GenerateRequest request, Path path, String content) throws IOException {
        if (Files.exists(path) && !request.overwrite()) {
            return new GeneratedFile(path, false);
        }
        Files.createDirectories(path.getParent());
        Files.writeString(path, content, StandardCharsets.UTF_8);
        return new GeneratedFile(path, true);
    }

    private static String renderEntity(TableMeta table) {
        Set<String> imports = new LinkedHashSet<>();
        imports.add("com.baomidou.mybatisplus.annotation.TableId");
        imports.add("com.baomidou.mybatisplus.annotation.TableName");
        imports.add("io.swagger.v3.oas.annotations.media.Schema");
        imports.add("lombok.Data");

        boolean hasTableField = table.columns().stream().anyMatch(DbCodeGenerator::needsTableField);
        boolean hasFieldFill = table.columns().stream().anyMatch(DbCodeGenerator::needsFieldFill);
        if (hasFieldFill) {
            imports.add("com.baomidou.mybatisplus.annotation.FieldFill");
        }
        if (hasTableField) {
            imports.add("com.baomidou.mybatisplus.annotation.TableField");
        }
        table.columns().stream()
                .map(DbCodeGenerator::javaType)
                .filter(DbCodeGenerator::needsJavaImport)
                .map(type -> "java." + javaImportPackage(type) + "." + type)
                .forEach(imports::add);

        StringBuilder builder = new StringBuilder();
        builder.append("package ").append(BASE_PACKAGE).append(".entity;\n\n");
        imports.stream().sorted().forEach(importName -> builder.append("import ").append(importName).append(";\n"));
        builder.append("\n@Data\n");
        builder.append("@TableName(\"").append(table.name()).append("\")\n");
        builder.append("@Schema(name = \"").append(table.entityName()).append("\", description = \"")
                .append(escape(table.description())).append("\")\n");
        builder.append("public class ").append(table.entityName()).append(" {\n");
        for (ColumnMeta column : table.columns()) {
            appendColumnAnnotations(builder, table, column);
            builder.append("    private ").append(javaType(column)).append(" ").append(column.fieldName()).append(";\n\n");
        }
        trimLastBlankLine(builder);
        builder.append("}\n");
        return builder.toString();
    }

    private static void appendColumnAnnotations(StringBuilder builder, TableMeta table, ColumnMeta column) {
        if (table.primaryKeys().contains(column.name())) {
            if (column.name().equals(column.fieldName())) {
                builder.append("    @TableId\n");
            } else {
                builder.append("    @TableId(\"").append(column.name()).append("\")\n");
            }
        } else if (needsTableField(column)) {
            builder.append("    @TableField");
            String fill = fieldFill(column);
            if (fill == null) {
                builder.append("(\"").append(column.name()).append("\")");
            } else {
                builder.append("(value = \"").append(column.name()).append("\", fill = ").append(fill).append(")");
            }
            builder.append("\n");
        }
        builder.append("    @Schema(description = \"").append(escape(column.description())).append("\")\n");
    }

    private static String renderMapper(TableMeta table) {
        return """
                package %s.mapper;

                import %s.entity.%s;
                import com.github.yulichang.base.MPJBaseMapper;
                import org.apache.ibatis.annotations.Mapper;

                @Mapper
                public interface %s extends MPJBaseMapper<%s> {
                }
                """.formatted(BASE_PACKAGE, BASE_PACKAGE, table.entityName(), table.mapperName(), table.entityName());
    }

    private static String renderDao(TableMeta table) {
        return """
                package %s.dao;

                import %s.entity.%s;
                import com.github.yulichang.base.MPJBaseService;

                public interface %s extends MPJBaseService<%s> {
                }
                """.formatted(BASE_PACKAGE, BASE_PACKAGE, table.entityName(), table.daoName(), table.entityName());
    }

    private static String renderDaoImpl(TableMeta table) {
        return """
                package %s.dao.impl;

                import %s.dao.%s;
                import %s.entity.%s;
                import %s.mapper.%s;
                import com.github.yulichang.base.MPJBaseServiceImpl;
                import org.springframework.stereotype.Repository;

                @Repository
                public class %s extends MPJBaseServiceImpl<%s, %s> implements %s {
                }
                """.formatted(BASE_PACKAGE, BASE_PACKAGE, table.daoName(), BASE_PACKAGE, table.entityName(),
                BASE_PACKAGE, table.mapperName(), table.daoImplName(), table.mapperName(), table.entityName(),
                table.daoName());
    }

    private static Path entityPath(GenerateRequest request, TableMeta table) {
        return request.projectRoot().resolve(SOURCE_ROOT).resolve(packagePath()).resolve("entity")
                .resolve(table.entityName() + ".java");
    }

    private static Path mapperPath(GenerateRequest request, TableMeta table) {
        return request.projectRoot().resolve(SOURCE_ROOT).resolve(packagePath()).resolve("mapper")
                .resolve(table.mapperName() + ".java");
    }

    private static Path daoPath(GenerateRequest request, TableMeta table) {
        return request.projectRoot().resolve(SOURCE_ROOT).resolve(packagePath()).resolve("dao")
                .resolve(table.daoName() + ".java");
    }

    private static Path daoImplPath(GenerateRequest request, TableMeta table) {
        return request.projectRoot().resolve(SOURCE_ROOT).resolve(packagePath()).resolve("dao").resolve("impl")
                .resolve(table.daoImplName() + ".java");
    }

    private static Path packagePath() {
        return Path.of(BASE_PACKAGE.replace(".", "/"));
    }

    private static boolean needsTableField(ColumnMeta column) {
        return !column.name().equals(column.fieldName()) || needsFieldFill(column);
    }

    private static boolean needsFieldFill(ColumnMeta column) {
        return fieldFill(column) != null;
    }

    private static String fieldFill(ColumnMeta column) {
        if (INSERT_FILL_COLUMNS.contains(column.name())) {
            return "FieldFill.INSERT";
        }
        if (INSERT_UPDATE_FILL_COLUMNS.contains(column.name())) {
            return "FieldFill.INSERT_UPDATE";
        }
        return null;
    }

    private static String javaType(ColumnMeta column) {
        return switch (column.dataType()) {
            case Types.BIT, Types.BOOLEAN -> "Boolean";
            case Types.TINYINT, Types.SMALLINT, Types.INTEGER -> "Integer";
            case Types.BIGINT -> "Long";
            case Types.FLOAT, Types.REAL -> "Float";
            case Types.DOUBLE -> "Double";
            case Types.NUMERIC, Types.DECIMAL -> "BigDecimal";
            case Types.DATE -> "LocalDate";
            case Types.TIME, Types.TIME_WITH_TIMEZONE -> "LocalTime";
            case Types.TIMESTAMP, Types.TIMESTAMP_WITH_TIMEZONE -> "LocalDateTime";
            case Types.BINARY, Types.VARBINARY, Types.LONGVARBINARY, Types.BLOB -> "byte[]";
            default -> "String";
        };
    }

    private static boolean needsJavaImport(String javaType) {
        return Set.of("BigDecimal", "LocalDate", "LocalTime", "LocalDateTime").contains(javaType);
    }

    private static String javaImportPackage(String javaType) {
        if ("BigDecimal".equals(javaType)) {
            return "math";
        }
        return "time";
    }

    private static String toClassName(String tableName) {
        return Arrays.stream(tableName.split("_"))
                .filter(part -> !part.isBlank())
                .map(DbCodeGenerator::capitalize)
                .collect(Collectors.joining());
    }

    private static String toFieldName(String columnName) {
        String className = toClassName(columnName);
        if (className.isBlank()) {
            return columnName;
        }
        return Character.toLowerCase(className.charAt(0)) + className.substring(1);
    }

    private static String capitalize(String value) {
        if (value.isBlank()) {
            return value;
        }
        String lower = value.toLowerCase(Locale.ROOT);
        return Character.toUpperCase(lower.charAt(0)) + lower.substring(1);
    }

    private static String escape(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private static void trimLastBlankLine(StringBuilder builder) {
        int length = builder.length();
        if (length >= 2 && builder.substring(length - 2).equals("\n\n")) {
            builder.setLength(length - 1);
        }
    }

    private static void printSummary(List<GeneratedFile> generatedFiles) {
        List<GeneratedFile> writtenFiles = generatedFiles.stream()
                .filter(GeneratedFile::written)
                .toList();
        System.out.println("Generated files: " + writtenFiles.size());
        for (GeneratedFile file : writtenFiles) {
            System.out.println("  " + file.path());
        }
        long skipped = generatedFiles.size() - writtenFiles.size();
        if (skipped > 0) {
            System.out.println("Skipped existing files: " + skipped);
        }
    }

    public record GenerateRequest(
            String jdbcUrl,
            String username,
            String password,
            Path projectRoot,
            String catalog,
            String schemaPattern,
            String tableNamePattern,
            Set<String> tableNames,
            boolean overwrite) {

        public GenerateRequest {
            Objects.requireNonNull(jdbcUrl, "jdbcUrl must not be null");
            Objects.requireNonNull(username, "username must not be null");
            password = password == null ? "" : password;
            projectRoot = projectRoot == null ? Path.of(".") : projectRoot;
            tableNamePattern = tableNamePattern == null || tableNamePattern.isBlank() ? "%" : tableNamePattern;
            tableNames = tableNames == null ? Set.of() : Set.copyOf(tableNames);
        }
    }

    public record GeneratedFile(Path path, boolean written) {
    }

    private record TableMeta(String name, String remarks, List<ColumnMeta> columns, Set<String> primaryKeys) {
        private String entityName() {
            return toClassName(name);
        }

        private String mapperName() {
            return toClassName(name) + "Mapper";
        }

        private String daoName() {
            return toClassName(name) + "Dao";
        }

        private String daoImplName() {
            return daoName() + "Impl";
        }

        private String description() {
            if (remarks == null || remarks.isBlank()) {
                return name + " data entity";
            }
            return remarks;
        }
    }

    private record ColumnMeta(
            String name,
            String fieldName,
            int dataType,
            String typeName,
            String remarks,
            boolean nullable) {

        private String description() {
            if (remarks == null || remarks.isBlank()) {
                return name;
            }
            return remarks;
        }
    }

    private static final class DriverShim implements Driver {
        private final Driver driver;

        private DriverShim(Driver driver) {
            this.driver = driver;
        }

        @Override
        public Connection connect(String url, Properties info) throws SQLException {
            return driver.connect(url, info);
        }

        @Override
        public boolean acceptsURL(String url) throws SQLException {
            return driver.acceptsURL(url);
        }

        @Override
        public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
            return driver.getPropertyInfo(url, info);
        }

        @Override
        public int getMajorVersion() {
            return driver.getMajorVersion();
        }

        @Override
        public int getMinorVersion() {
            return driver.getMinorVersion();
        }

        @Override
        public boolean jdbcCompliant() {
            return driver.jdbcCompliant();
        }

        @Override
        public Logger getParentLogger() throws SQLFeatureNotSupportedException {
            return driver.getParentLogger();
        }
    }
}
