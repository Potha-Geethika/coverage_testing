package com.project.runner;

    import java.io.File;

    import org.testng.annotations.Listeners;
    import org.testng.annotations.Test;

    import com.aventstack.extentreports.testng.listener.ExtentITestListenerClassAdapter;

    import io.cucumber.testng.AbstractTestNGCucumberTests;

    @Listeners({ExtentITestListenerClassAdapter.class})
    public class TestRunner extends AbstractTestNGCucumberTests {
        
        @Test(priority = 0)
        public void runLoginscreenforrecruiters() {
            runCucumberFeature("src/test/resources/com/coverage_testing/features/LoginScreenForRecruiters.feature");
        }
    
            
    private void runCucumberFeature(String featurePath) {
            String featureName = new File(featurePath).getName().replace(".feature", "");

            String[] argv = new String[] {
                featurePath,
                "--plugin", "pretty",
                "--plugin", "html:target/cucumber-reports/html-" + featureName + ".html",
                "--plugin", "json:target/cucumber-reports/" + featureName + ".json",
                "--plugin", "junit:target/cucumber-reports/" + featureName + ".xml"
            };

            io.cucumber.core.cli.Main.run(argv, Thread.currentThread().getContextClassLoader());
        }
    }
    