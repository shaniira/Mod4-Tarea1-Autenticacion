package com.andinaseguros.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(packages = "com.andinaseguros", importOptions = ImportOption.DoNotIncludeTests.class)
class OnionArchitectureTest {
    @ArchTest
    static final ArchRule domain_is_the_independent_center =
            noClasses().that().resideInAPackage("..domain..")
                    .should().dependOnClassesThat()
                    .resideInAnyPackage("..application..", "..infrastructure..", "..presentation..");

    @ArchTest
    static final ArchRule application_only_points_to_domain =
            noClasses().that().resideInAPackage("..application..")
                    .should().dependOnClassesThat()
                    .resideInAnyPackage("..infrastructure..", "..presentation..");

    @ArchTest
    static final ArchRule application_has_no_framework_dependencies =
            noClasses().that().resideInAPackage("..application..")
                    .should().dependOnClassesThat()
                    .resideInAnyPackage(
                            "org.springframework..",
                            "jakarta.persistence..",
                            "jakarta.validation..",
                            "org.bson..",
                            "com.mongodb..");

    @ArchTest
    static final ArchRule onion_repository_contracts_are_domain_interfaces =
            classes().that().resideInAPackage("..domain.repository..")
                    .should().beInterfaces();

    @ArchTest
    static final ArchRule onion_gateway_contracts_are_application_interfaces =
            classes().that().resideInAPackage("..application.gateway..").and().haveSimpleNameEndingWith("Port")
                    .should().beInterfaces();

    @ArchTest
    static final ArchRule onion_repository_implementations_live_in_infrastructure =
            classes().that().implement(com.tngtech.archunit.base.DescribedPredicate.describe(
                            "a domain repository interface",
                            clazz -> clazz.getPackageName().contains(".domain.repository")))
                    .should().resideInAPackage("..infrastructure..");

    @ArchTest
    static final ArchRule onion_gateway_implementations_live_in_infrastructure =
            classes().that().implement(com.tngtech.archunit.base.DescribedPredicate.describe(
                            "an application gateway port",
                            clazz -> clazz.getPackageName().contains(".application.gateway")
                                    && clazz.getSimpleName().endsWith("Port")))
                    .should().resideInAPackage("..infrastructure..");

    @ArchTest
    static final ArchRule onion_has_no_port_packages =
            noClasses().should().resideInAPackage("..port..");

    @ArchTest
    static final ArchRule domain_has_no_framework_dependencies =
            noClasses().that().resideInAPackage("..domain..")
                    .should().dependOnClassesThat()
                    .resideInAnyPackage("org.springframework..", "org.springframework.data..");
}