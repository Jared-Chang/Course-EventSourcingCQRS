package ntut.csie.sslab.ezkanban.kanban.common.usecase;

import ntut.csie.sslab.ezkanban.kanban.main.framework.springboot.web.config.UseCaseInjection;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;


// 不能使用這個 annotation，用了之後 OR Mapping 的測試結果是不正確的
//@DataJpaTest

//@RunWith(SpringRunner.class)
//@SpringBootTest

// 加上這個就不會每次跑完test method自動 rollback，才可以看到測試資料被加入資料庫
@Rollback(false)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes= JpaApplicationTestContext.class)
@TestPropertySource(locations = "classpath:board-test.properties")
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureAfter({UseCaseInjection.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class AbstractSpringBootJpaTest extends SharedSpringBootJpaTest {

}
