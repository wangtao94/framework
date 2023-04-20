package db;

import com.github.davidfantasy.mybatisplus.generatorui.GeneratorConfig;
import com.github.davidfantasy.mybatisplus.generatorui.MybatisPlusToolsApplication;
import com.github.davidfantasy.mybatisplus.generatorui.mbp.NameConverter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class GeneratorUIServer {

    public static void main(String[] args) {
        GeneratorConfig generatorConfig = readProp();
        MybatisPlusToolsApplication.run(generatorConfig);
        System.out.println(String.format(
                """
                        当前数据库连接信息：%s
                        当前库名：%s
                        MybatisPlus代码生成器启动成功，访问地址：http://localhost:%s
                        """,
                generatorConfig.getJdbcUrl(),
                generatorConfig.getSchemaName(),
                generatorConfig.getPort()));
    }


    /**
     * 从配置文件中读取配置.
     * 路径为：/db/mbg.properties
     */
    private static GeneratorConfig readProp() {
        InputStream is = GeneratorUIServer.class.getResourceAsStream("/db/mbg/mbg.properties");
        Properties prop = new Properties();
        try {
            prop.load(is);
        } catch (IOException e) {
            throw new RuntimeException("读取配置文件失败", e);
        }
        String jdbcUrl = prop.getProperty("jdbc.url", "jdbc:mysql://localhost:3306/example");
        GeneratorConfig config = GeneratorConfig.builder()
                .jdbcUrl(jdbcUrl)
                .userName(prop.getProperty("jdbc.username", "root"))
                .password(prop.getProperty("jdbc.password", "root"))
                .driverClassName(prop.getProperty("jdbc.driverClassName", "com.mysql.cj.jdbc.Driver"))
                //数据库schema，MSSQL,PGSQL,ORACLE,DB2类型的数据库需要指定
                .schemaName(prop.getProperty("jdbc.url", jdbcUrl).split("/")[3])
                .basePackage(prop.getProperty("basePackage", "cn.trve.example"))
                .port(Integer.parseInt(prop.getProperty("port", "8068")))
                .nameConverter(new NameConverter() {
                    @Override
                    public String serviceNameConvert(String entityName) {
                        return entityName + "Service";
                    }
                })
                .build();

        return config;
    }


}