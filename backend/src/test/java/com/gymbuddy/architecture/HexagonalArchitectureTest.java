package com.gymbuddy.architecture;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.junit.CacheMode;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import jakarta.persistence.Entity;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

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
}
