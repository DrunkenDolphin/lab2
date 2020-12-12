package ru.mirea.pois;


import lombok.extern.slf4j.Slf4j;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderError;
import org.kie.internal.builder.KnowledgeBuilderErrors;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import ru.mirea.pois.data.*;

import java.io.File;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class Main {

    private static void loadDataIntoSession(KieSession kieSession, LocalDate dayToConsider) {

        List<Property> propertyList = Arrays.asList(new Property(PropertyType.REALTY, 130000),
                new Property(PropertyType.TRANSPORT, 20000), new Property(PropertyType.SECURITIES, 100000));

        Client client = new Client("John",  20000, propertyList, 5, "married", ClientType.MAIN);

        Client client1 = new Client("Maria", 15000, propertyList, 4, "married", ClientType.GUARANTOR);

        kieSession.insert(propertyList);

        kieSession.insert(client);

        kieSession.insert(dayToConsider);

        kieSession.insert(client1);

    }

    private static InternalKnowledgeBase readKnoledgeBase(List<File> files) {
        KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        files.forEach(i -> knowledgeBuilder.add(ResourceFactory.newFileResource(i), ResourceType.DRL));

        KnowledgeBuilderErrors errors = knowledgeBuilder.getErrors();

        if (!errors.isEmpty()) {
            for (KnowledgeBuilderError error : errors) {
                log.error("{}", error);
            }
            throw new IllegalArgumentException("Could not parse knowledge.");
        }

        InternalKnowledgeBase kBase = KnowledgeBaseFactory.newKnowledgeBase();
        kBase.addPackages(knowledgeBuilder.getKnowledgePackages());

        return kBase;
    }


    public static void showSending(KieSession kieSession) {
        System.out.println("Showing results");
        log.info("{}", kieSession);
    }

    public static void main(String[] args) {
        try {
            InternalKnowledgeBase kBase = readKnoledgeBase(Arrays.asList(
                    new File("rules/calcCredit.drl")));
            KieSession kSession = kBase.newKieSession();

            LocalDate dayToConsider = LocalDate.now();
            loadDataIntoSession(kSession, dayToConsider);

            kSession.fireAllRules();

            showSending(kSession);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
