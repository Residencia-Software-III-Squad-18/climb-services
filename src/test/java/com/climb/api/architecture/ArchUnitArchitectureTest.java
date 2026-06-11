package com.climb.api.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.fields;

@AnalyzeClasses(packages = "com.climb.api")
public class ArchUnitArchitectureTest {

    private static final JavaClasses CLASSES =
            new ClassFileImporter().importPackages("com.climb.api");

    private static final ArchRule controllers_should_be_named_and_annotated = classes()
            .that().resideInAPackage("..controller..")
            .and().haveSimpleNameNotEndingWith("Test")
            .should().haveSimpleNameEndingWith("Controller")
            .andShould().beAnnotatedWith(org.springframework.web.bind.annotation.RestController.class)
            .andShould().bePublic();

    private static final ArchRule repositories_should_be_interfaces = classes()
            .that().resideInAPackage("..repository..")
            .should().beInterfaces();

    private static final ArchRule models_should_be_entities = classes()
            .that().resideInAPackage("..model..")
            .and().resideOutsideOfPackages("..model.dto..", "..model.enums..")
            .and().areNotEnums()
            .should().beAnnotatedWith(jakarta.persistence.Entity.class);

    private static final ArchRule no_public_fields = fields()
            .that().areDeclaredInClassesThat().resideInAPackage("com.climb.api..")
            .and().areDeclaredInClassesThat().haveSimpleNameNotEndingWith("Test")
            .and().areNotStatic()
            .should().notBePublic()
            .allowEmptyShould(true);

    @Test
    void controllers_should_be_named_and_annotated() {
        controllers_should_be_named_and_annotated.check(CLASSES);
    }

    @Test
    void repositories_should_be_interfaces() {
        repositories_should_be_interfaces.check(CLASSES);
    }

    @Test
    void models_should_be_entities() {
        models_should_be_entities.check(CLASSES);
    }

    @Test
    void no_public_fields() {
        no_public_fields.check(CLASSES);
    }
}