package ntut.csie.sslab.ezkanban.kanban.main.framework.springboot.web.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix="ezkanban")
public class DataSourceConfig {
    private String dataSource;

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }
}
