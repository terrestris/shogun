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
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "de.terrestris", importOptions = { ImportOption.DoNotIncludeTests.class })
public class DependencyRulesTest {

    @ArchTest
    static final ArchRule services_should_not_access_controllers =
        noClasses()
            .that().areAnnotatedWith(Service.class)
            .should().accessClassesThat().areAnnotatedWith(RestController.class)
            .orShould().accessClassesThat().areAnnotatedWith(Controller.class);

    @ArchTest
    static final ArchRule services_should_not_depend_on_controllers =
        noClasses()
            .that().areAnnotatedWith(Service.class)
            .should().dependOnClassesThat().areAnnotatedWith(RestController.class)
            .orShould().dependOnClassesThat().areAnnotatedWith(Controller.class);

    @ArchTest
    static final ArchRule repositories_should_not_access_controllers =
        noClasses()
            .that().areAnnotatedWith(Repository.class)
            .should().accessClassesThat().areAnnotatedWith(RestController.class)
            .orShould().accessClassesThat().areAnnotatedWith(Controller.class);


    @ArchTest
    static final ArchRule repositories_should_not_depend_on_controllers =
        noClasses()
            .that().areAnnotatedWith(Repository.class)
            .should().dependOnClassesThat().areAnnotatedWith(RestController.class)
            .orShould().dependOnClassesThat().areAnnotatedWith(Controller.class);

    @ArchTest
    static final ArchRule components_should_not_access_controllers =
        noClasses()
            .that().areAnnotatedWith(Component.class)
            .should().accessClassesThat().areAnnotatedWith(RestController.class)
            .orShould().accessClassesThat().areAnnotatedWith(Controller.class);

    @ArchTest
    static final ArchRule components_should_not_depend_on_controllers =
        noClasses()
            .that().areAnnotatedWith(Component.class)
            .should().dependOnClassesThat().areAnnotatedWith(RestController.class)
            .orShould().dependOnClassesThat().areAnnotatedWith(Controller.class);

    @ArchTest
    static final ArchRule permission_evaluators_should_not_access_services =
        noClasses()
            .that().implement(EntityPermissionEvaluator.class)
            // required to exclude BaseEntityPermissionEvaluator
            .and().doNotHaveFullyQualifiedName("de.terrestris.shogun.lib.security.access.entity.BaseEntityPermissionEvaluator")
            .should().accessClassesThat().areAnnotatedWith(Service.class);

    @ArchTest
    static final ArchRule permission_evaluators_should_not_depend_on_services =
        noClasses()
            .that().implement(EntityPermissionEvaluator.class)
            // required to exclude BaseEntityPermissionEvaluator
            .and().doNotHaveFullyQualifiedName("de.terrestris.shogun.lib.security.access.entity.BaseEntityPermissionEvaluator")
            .should().dependOnClassesThat().areAnnotatedWith(Service.class);

//      todo: enable when UserProviderService / GroupProviderService are available
//    @ArchTest
//    static final ArchRule group_provider_services_should_not_access_services =
//        noClasses()
//            .that().implement(GroupProviderService.class)
//            .should().accessClassesThat().areAnnotatedWith(Service.class);
//
//    @ArchTest
//    static final ArchRule user_provider_services_should_not_access_services =
//        noClasses()
//            .that().implement(UserProviderService.class)
//            .should().accessClassesThat().areAnnotatedWith(Service.class);

}

