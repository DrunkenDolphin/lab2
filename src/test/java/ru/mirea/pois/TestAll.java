package ru.mirea.pois;

import lombok.extern.slf4j.Slf4j;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.junit.Assert;
import org.junit.Test;
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
public class TestAll {

    @Test
    public void testNotNull() {
        try {
            InternalKnowledgeBase kBase = prepareKnowledgeBase(Arrays.asList(
                    new File("rules/calcCredit.drl")));
            KieSession kSession = kBase.newKieSession();


            LocalDate dayToConsider = LocalDate.now();

            List<Property> propertyList = Arrays.asList(new Property(PropertyType.REALTY, 130000),
                    new Property(PropertyType.TRANSPORT, 20000), new Property(PropertyType.SECURITIES, 100000));

            Client client = new Client("John",  20000, propertyList, 5, "married", ClientType.MAIN);

            Client guarantor = new Client("Maria", 15000, propertyList, 4, "married", ClientType.GUARANTOR);

            loadDataIntoSession(kSession, dayToConsider, client, guarantor);

            kSession.fireAllRules();
            Credit credit = (Credit) kSession.getGlobal("credit");

            Assert.assertEquals(0.14, credit.getPercent(), 0.0);
            Assert.assertEquals(285000, credit.getSum());
            Assert.assertEquals(LocalDate.now(), credit.getDueTime());

            log.info("Процентнтая ставка = {}", credit.getPercent());
            log.info("Сумма ипотеки = {}", credit.getSum());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testNull() {
        try {
            InternalKnowledgeBase kBase = prepareKnowledgeBase(Arrays.asList(
                    new File("rules/calcCredit.drl")));
            KieSession kSession = kBase.newKieSession();

            List<Property> propertyList = Arrays.asList(new Property(PropertyType.REALTY, 130000),
                    new Property(PropertyType.TRANSPORT, 20000), new Property(PropertyType.SECURITIES, 100000));

            Client client = new Client("John",  20000, propertyList, 2, "married", ClientType.MAIN);

            Client guarantor = new Client("Maria", 15000, propertyList, 3, "married", ClientType.GUARANTOR);

            LocalDate dayToConsider = LocalDate.now();
            loadDataIntoSession(kSession, dayToConsider, client, guarantor);

            kSession.fireAllRules();
            Credit credit = (Credit) kSession.getGlobal("credit");

            Assert.assertNull(credit.getClient());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadDataIntoSession(KieSession kieSession, LocalDate dayToConsider, Client client, Client guarantor) {


        Credit credit = new Credit();

        kieSession.setGlobal("credit", credit);

        kieSession.insert(client);

        kieSession.insert(dayToConsider);

        kieSession.insert(guarantor);

    }

    private InternalKnowledgeBase prepareKnowledgeBase(List<File> files) {
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
}
