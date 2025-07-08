
package org.example.tmsstriker;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * Повний набір тестів для TMS Striker API
 */
@Suite
@SelectClasses({
        // Основні тести
        ProjectApiTest.class,
        TestCaseApiTest.class,
        TestSuiteApiTest.class,
        FullFlowIntegrationTest.class,

        // Нові тести контролерів
        AppUserControllerTest.class,
        ConfigurationControllerTest.class,
        EnvironmentControllerTest.class,
        VersionControllerTest.class,
        TestRunControllerTest.class,
        TestRunCaseResultControllerTest.class,

        // Спеціальні тести
        SpecialApiTests.class
})
public class CompleteApiTestSuite {
    // JUnit 5 автоматично запустить всі тести
}