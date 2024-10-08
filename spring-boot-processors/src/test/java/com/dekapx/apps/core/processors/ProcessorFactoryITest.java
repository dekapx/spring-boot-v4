package com.dekapx.apps.core.processors;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.dekapx.apps.core.utils.BeanUtils.generateServiceLocatorLookupBeanName;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
public class ProcessorFactoryITest {
    @Autowired
    private ProcessorFactory processorFactory;

    @Test
    public void shouldReturnProcessorForGivenProcessorBeanName() {
        Processor processor = this.processorFactory.getProcessor(generateServiceLocatorLookupBeanName(DummyProcessor.class));
        assertThat(processor).isNotNull()
                .satisfies(o -> {
                    assertThat(o).isInstanceOf(DummyProcessor.class);
                });
        String[] values = {"Test", "Value"};
        processor.process(values);
    }
}
