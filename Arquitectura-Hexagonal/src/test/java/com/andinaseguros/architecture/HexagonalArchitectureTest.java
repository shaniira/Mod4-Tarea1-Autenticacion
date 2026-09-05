package com.andinaseguros.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(packages = "com.andinaseguros", importOptions = ImportOption.DoNotIncludeTests.class)
class HexagonalArchitectureTest {
    @ArchTest
    static final ArchRule core_is_framework_independent =
            noClasses().that().resideInAPackage("..core..")
                    .should().dependOnClassesThat()
                    .resideInAnyPackage(
                            "..adapters..",
                            "..bootstrap..",
                            "org.springframework..",
                            "org.bson..",
                            "com.mongodb..",
                            "jakarta..",
                            "io.jsonwebtoken..",
                            "com.fasterxml..",
                            "org.springdoc..");

    @ArchTest
    static final ArchRule domain_does_not_depend_on_application_or_ports =
            noClasses().that().resideInAPackage("..core.domain..")
                    .should().dependOnClassesThat()
                    .resideInAnyPackage("..core.application..", "..core.ports..");

    @ArchTest
    static final ArchRule domain_has_no_repository_package =
            noClasses().should().resideInAPackage("..core.domain.repository..");

    @ArchTest
    static final ArchRule repository_ports_are_core_interfaces =
            classes().that().resideInAPackage("..core.ports.out.persistence..")
                    .and().haveSimpleNameEndingWith("RepositoryPort")
                    .should().beInterfaces();

    @ArchTest
    static final ArchRule outbound_ports_are_interfaces =
            classes().that().resideInAPackage("..core.ports.out..")
                    .and().haveSimpleNameEndingWith("Port")
                    .should().beInterfaces();

    @ArchTest
    static final ArchRule inbound_ports_are_interfaces =
            classes().that().resideInAPackage("..core.ports.in..")
                    .should().beInterfaces();

    @ArchTest
    static final ArchRule outbound_port_implementations_live_in_adapters =
            classes().that().implement(com.tngtech.archunit.base.DescribedPredicate.describe(
                            "an outbound port",
                            clazz -> clazz.getPackageName().contains(".core.ports.out")
                                    && clazz.getSimpleName().endsWith("Port")))
                    .should().resideInAPackage("..adapters..");

    @ArchTest
    static final ArchRule inbound_port_implementations_live_in_core_application =
            classes().that().implement(com.tngtech.archunit.base.DescribedPredicate.describe(
                            "an inbound port (use case)",
                            clazz -> clazz.getPackageName().contains(".core.ports.in")))
                    .should().resideInAPackage("..core.application..");

    @ArchTest
    static final ArchRule rest_controllers_are_inbound_adapters =
            classes().that().haveSimpleNameEndingWith("Controller")
                    .should().resideInAPackage("..adapters.inbound.rest.controller..");

}
