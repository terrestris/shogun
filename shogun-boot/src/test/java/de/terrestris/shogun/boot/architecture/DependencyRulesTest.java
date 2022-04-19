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

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.*;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "de.terrestris")
public class DependencyRulesTest {

    @ArchTest
    static final ArchRule services_should_not_access_controllers =
        noClasses().that().resideInAPackage("..service..")
            .should().accessClassesThat().resideInAPackage("..controller..");

    @ArchTest
    static final ArchRule permission_evaluators_should_not_access_services =
        noClasses().that().resideInAPackage("de.terrestris.progemis.security.entity")
            .should().accessClassesThat().resideInAPackage("..service..");

    @ArchTest
    static final ArchRule services_should_not_depend_on_controllers =
        noClasses().that().resideInAPackage("..service..")
            .should().dependOnClassesThat().resideInAPackage("..controller..");

    @ArchTest
    static final ArchRule permission_evaluators_should_not_depend_on_services =
        noClasses().that().resideInAPackage("de.terrestris.progemis.security.entity")
            .should().dependOnClassesThat().resideInAPackage("..service..");
            // todo: replace progemis
            // todo: more specific selector than resideInAPackage

}

