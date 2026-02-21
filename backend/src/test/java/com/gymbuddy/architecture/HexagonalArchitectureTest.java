package com.gymbuddy.architecture;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.junit.CacheMode;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import jakarta.persistence.Entity;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noFields;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

@AnalyzeClasses(packages = "com.gymbuddy", importOptions = ImportOption.DoNotIncludeTests.class, cacheMode = CacheMode.PER_CLASS)
class HexagonalArchitectureTest {

  @ArchTest
  static final ArchRule domain_should_not_depend_on_application_or_adapters =
      noClasses()
          .that()
          .resideInAPackage("..domain..")
          .should()
          .dependOnClassesThat()
          .resideInAnyPackage("..application..", "..adapter..");

  @ArchTest
  static final ArchRule application_should_not_depend_on_adapters =
      noClasses()
          .that()
          .resideInAPackage("..application..")
          .should()
          .dependOnClassesThat()
          .resideInAPackage("..adapter..");

  @ArchTest
  static final ArchRule domain_should_only_depend_on_allowlisted_packages =
      classes()
          .that()
          .resideInAPackage("..domain..")
          .should()
          .onlyDependOnClassesThat()
          .resideInAnyPackage("java..", "javax..", "lombok..", "org.slf4j..", "..domain..");

  @ArchTest
  static final ArchRule rest_controllers_must_live_in_inbound_adapter_package =
      classes()
          .that()
          .areAnnotatedWith(RestController.class)
          .should()
          .resideInAPackage("..adapter.inbound..");

  @ArchTest
  static final ArchRule jpa_entities_must_live_in_outbound_persistence_package =
      classes()
          .that()
          .areAnnotatedWith(Entity.class)
          .should()
          .resideInAPackage("..adapter.outbound.persistence..");

  @ArchTest
  static final ArchRule inbound_adapters_should_not_depend_on_application_services =
      noClasses()
          .that()
          .resideInAPackage("..adapter.inbound..")
          .should()
          .dependOnClassesThat()
          .resideInAPackage("..application.service..");

  @ArchTest
  static final ArchRule outbound_adapters_should_not_depend_on_inbound_adapters =
      noClasses()
          .that()
          .resideInAPackage("..adapter.outbound..")
          .should()
          .dependOnClassesThat()
          .resideInAPackage("..adapter.inbound..");

  @ArchTest
  static final ArchRule application_ports_should_be_interfaces =
      classes()
          .that()
          .resideInAPackage("..application.port..")
          .should()
          .beInterfaces();

  @ArchTest
  static final ArchRule jpa_repositories_should_live_in_outbound_persistence_package =
      classes()
          .that()
          .areAssignableTo(JpaRepository.class)
          .should()
          .resideInAPackage("..adapter.outbound.persistence..");

  @ArchTest
  static final ArchRule no_field_injection_with_autowired =
      noFields()
          .that()
          .areDeclaredInClassesThat()
          .haveSimpleNameNotEndingWith("MapperImpl")
          .should()
          .beAnnotatedWith(Autowired.class);

  @ArchTest
  static final ArchRule package_slices_should_be_free_of_cycles =
      slices().matching("com.gymbuddy.(*)..").should().beFreeOfCycles();
}
