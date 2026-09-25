package com.projectname.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noFields;
import static com.tngtech.archunit.library.Architectures.onionArchitecture;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.junit.CacheMode;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import jakarta.persistence.Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.RestController;

@AnalyzeClasses(
        packages = "com.projectname",
        importOptions = ImportOption.DoNotIncludeTests.class,
        cacheMode = CacheMode.PER_CLASS)
class HexagonalArchitectureTest {

    @ArchTest
    static final ArchRule onion_layers = onionArchitecture()
            .domainModels("..domain..")
            .applicationServices("..application..")
            .adapter("http", "..adapter.inbound..", "..adapter.config..")
            .adapter("outbound", "..adapter.outbound..")
            .withOptionalLayers(true);

    @ArchTest
    static final ArchRule domain_should_only_depend_on_allowlisted_packages = classes()
            .that()
            .resideInAPackage("..domain..")
            .should()
            .onlyDependOnClassesThat()
            .resideInAnyPackage("java..", "javax..", "lombok..", "org.slf4j..", "..domain..");

    @ArchTest
    static final ArchRule rest_controllers_must_live_in_inbound_adapter_package =
            classes().that().areAnnotatedWith(RestController.class).should().resideInAPackage("..adapter.inbound..");

    @ArchTest
    static final ArchRule jpa_entities_must_live_in_outbound_persistence_package = classes()
            .that()
            .areAnnotatedWith(Entity.class)
            .should()
            .resideInAPackage("..adapter.outbound.persistence..");

    @ArchTest
    static final ArchRule inbound_adapters_should_not_depend_on_application_services = noClasses()
            .that()
            .resideInAPackage("..adapter.inbound..")
            .should()
            .dependOnClassesThat()
            .resideInAPackage("..application.usecase..");

    @ArchTest
    static final ArchRule controllers_must_not_depend_on_outbound_ports = noClasses()
            .that()
            .resideInAPackage("..adapter.inbound..")
            .should()
            .dependOnClassesThat()
            .resideInAPackage("..application.port.out..");

    @ArchTest
    static final ArchRule outbound_adapters_should_not_depend_on_inbound_adapters = noClasses()
            .that()
            .resideInAPackage("..adapter.outbound..")
            .should()
            .dependOnClassesThat()
            .resideInAPackage("..adapter.inbound..");

    @ArchTest
    static final ArchRule outbound_adapters_should_not_depend_on_inbound_ports = noClasses()
            .that()
            .resideInAPackage("..adapter.outbound..")
            .should()
            .dependOnClassesThat()
            .resideInAPackage("..application.port.in..");

    @ArchTest
    static final ArchRule outbound_adapters_should_not_depend_on_usecases = noClasses()
            .that()
            .resideInAPackage("..adapter.outbound..")
            .should()
            .dependOnClassesThat()
            .resideInAPackage("..application.usecase..");

    @ArchTest
    static final ArchRule application_ports_should_be_interfaces = classes()
            .that()
            .resideInAnyPackage("..application.port.in..", "..application.port.out..")
            .should()
            .beInterfaces();

    @ArchTest
    static final ArchRule inbound_ports_have_one_invoke_method = classes()
            .that()
            .resideInAPackage("..application.port.in..")
            .should(new ArchCondition<>("declare only invoke(…) as their operation") {
                @Override
                public void check(final JavaClass port, final ConditionEvents events) {
                    final var methods = port.getMethods();
                    final var valid = methods.size() == 1
                            && methods.iterator().next().getName().equals("invoke");
                    events.add(
                            new SimpleConditionEvent(port, valid, port.getName() + " must declare one invoke method"));
                }
            });

    @ArchTest
    static final ArchRule inbound_ports_are_commands_or_queries = classes()
            .that()
            .resideInAPackage("..application.port.in..")
            .should(new ArchCondition<>("be named *Command or *Query") {
                @Override
                public void check(final JavaClass port, final ConditionEvents events) {
                    final var name = port.getSimpleName();
                    final var valid = name.endsWith("Command") || name.endsWith("Query");
                    events.add(new SimpleConditionEvent(port, valid, name + " must be a command or query"));
                }
            });

    @ArchTest
    static final ArchRule usecases_implement_their_matching_inbound_port = classes()
            .that()
            .resideInAPackage("..application.usecase..")
            .should(new ArchCondition<>("implement the matching inbound port") {
                @Override
                public void check(final JavaClass usecase, final ConditionEvents events) {
                    final var expectedPort = "com.projectname.application.port.in."
                            + usecase.getSimpleName().replaceFirst("Impl$", "");
                    final var valid = usecase.getSimpleName().endsWith("Impl")
                            && usecase.getRawInterfaces().size() == 1
                            && usecase.getRawInterfaces()
                                    .iterator()
                                    .next()
                                    .getName()
                                    .equals(expectedPort);
                    events.add(new SimpleConditionEvent(
                            usecase, valid, usecase.getName() + " must implement " + expectedPort));
                }
            });

    @ArchTest
    static final ArchRule queries_do_not_write_through_outbound_ports = methods()
            .that()
            .areDeclaredInClassesThat()
            .resideInAPackage("..application.usecase..")
            .and()
            .areDeclaredInClassesThat()
            .haveSimpleNameEndingWith("QueryImpl")
            .should(new ArchCondition<>("not invoke a write operation on an outbound port") {
                @Override
                public void check(final JavaMethod method, final ConditionEvents events) {
                    final var writes = method.getMethodCallsFromSelf().stream()
                            .filter(call -> call.getTargetOwner()
                                            .getPackageName()
                                            .equals("com.projectname.application.port.out")
                                    && (call.getTarget().getName().startsWith("save")
                                            || call.getTarget().getName().startsWith("delete")))
                            .toList();
                    events.add(new SimpleConditionEvent(
                            method,
                            writes.isEmpty(),
                            method.getFullName() + " must not write through an outbound port"));
                }
            });

    @ArchTest
    static final ArchRule rest_endpoints_call_one_inbound_port = methods()
            .that()
            .areDeclaredInClassesThat()
            .areAnnotatedWith(RestController.class)
            .and()
            .arePublic()
            .should(new ArchCondition<>("call exactly one inbound command or query") {
                @Override
                public void check(final JavaMethod method, final ConditionEvents events) {
                    final var calls = method.getMethodCallsFromSelf().stream()
                            .filter(call -> call.getTargetOwner()
                                    .getPackageName()
                                    .equals("com.projectname.application.port.in"))
                            .toList();
                    final var valid = calls.size() == 1
                            && calls.getFirst().getTarget().getName().equals("invoke");
                    events.add(new SimpleConditionEvent(
                            method, valid, method.getFullName() + " must call exactly one inbound port via invoke"));
                }
            });

    @ArchTest
    static final ArchRule only_persistence_uses_jpa = noClasses()
            .that()
            .resideOutsideOfPackage("..adapter.outbound.persistence..")
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage("jakarta.persistence..", "org.springframework.data.jpa..");

    @ArchTest
    static final ArchRule only_jpa_entities_use_persistence_annotations = noClasses()
            .that()
            .resideInAPackage("..adapter.outbound.persistence..")
            .and()
            .haveSimpleNameNotEndingWith("JpaEntity")
            .should()
            .dependOnClassesThat()
            .resideInAPackage("jakarta.persistence..");

    @ArchTest
    static final ArchRule only_jpa_repositories_use_spring_data_jpa = noClasses()
            .that()
            .resideInAPackage("..adapter.outbound.persistence..")
            .and()
            .haveSimpleNameNotEndingWith("JpaRepository")
            .should()
            .dependOnClassesThat()
            .resideInAPackage("org.springframework.data.jpa..");

    @ArchTest
    static final ArchRule only_repository_adapters_access_jpa_repositories = noClasses()
            .that()
            .resideInAPackage("..adapter.outbound.persistence..")
            .and()
            .haveSimpleNameNotEndingWith("RepositoryAdapter")
            .and()
            .haveSimpleNameNotEndingWith("JpaRepository")
            .should()
            .dependOnClassesThat()
            .haveSimpleNameEndingWith("JpaRepository");

    @ArchTest
    static final ArchRule repository_adapters_implement_outbound_ports = classes()
            .that()
            .haveSimpleNameEndingWith("RepositoryAdapter")
            .should(new ArchCondition<>("implement an outbound port") {
                @Override
                public void check(final JavaClass adapter, final ConditionEvents events) {
                    final var valid = adapter.getRawInterfaces().size() == 1
                            && adapter.getRawInterfaces()
                                    .iterator()
                                    .next()
                                    .getPackageName()
                                    .equals("com.projectname.application.port.out");
                    events.add(new SimpleConditionEvent(
                            adapter, valid, adapter.getName() + " must implement one outbound port"));
                }
            });

    @ArchTest
    static final ArchRule jpa_repositories_should_live_in_outbound_persistence_package = classes()
            .that()
            .areAssignableTo(JpaRepository.class)
            .should()
            .resideInAPackage("..adapter.outbound.persistence..");

    @ArchTest
    static final ArchRule no_field_injection_with_autowired = noFields()
            .that()
            .areDeclaredInClassesThat()
            .haveSimpleNameNotEndingWith("MapperImpl")
            .should()
            .beAnnotatedWith(Autowired.class);

    @ArchTest
    static final ArchRule package_slices_should_be_free_of_cycles =
            slices().matching("com.projectname.(*)..").should().beFreeOfCycles();
}
