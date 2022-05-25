/* SHOGun, https://terrestris.github.io/shogun/
 *
 * Copyright © 2020-present terrestris GmbH & Co. KG
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   https://www.apache.org/licenses/LICENSE-2.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.terrestris.shogun.boot.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import de.terrestris.shogun.lib.security.access.entity.EntityPermissionEvaluator;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

@AnalyzeClasses(packages = "de.terrestris", importOptions = { ImportOption.DoNotIncludeTests.class })
public class NamingConventionsTest {

    @ArchTest
    static final ArchRule services_should_be_named_correctly =
        classes()
            .that().areAnnotatedWith(Service.class)
            .should().haveSimpleNameEndingWith("Service");

    @ArchTest
    static final ArchRule controllers_should_be_named_correctly =
        classes()
            .that().areAnnotatedWith(Controller.class)
            .should().haveSimpleNameEndingWith("Controller");

    @ArchTest
    static final ArchRule rest_controllers_should_be_named_correctly =
        classes()
            .that().areAnnotatedWith(RestController.class)
            .should().haveSimpleNameEndingWith("Controller");

    @ArchTest
    static final ArchRule repositories_should_be_named_correctly =
        classes()
            .that().areAnnotatedWith(Repository.class)
            .should().haveSimpleNameEndingWith("Repository");

    @ArchTest
    static final ArchRule permission_evaluators_should_be_named_correctly =
        classes()
            .that().implement(EntityPermissionEvaluator.class)
            .should().haveSimpleNameEndingWith("PermissionEvaluator");

}

