package com.dekapx.apps.core.processors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;

import static com.dekapx.apps.core.utils.BeanUtils.generateServiceLocatorLookupBeanName;

@Slf4j
@Component
public class ProcessorExecutorImpl implements ProcessorExecutor {
    @Autowired
    private ProcessorFactory processorFactory;

    @Override
    public void execute(Class[] processorTypes, Object[] args) {
        toProcessorBeans(processorTypes).forEach(p -> p.process(args));
    }

    private List<Processor> toProcessorBeans(Class[] processorTypes) {
        final List<Processor> processors = new LinkedList<>();
        Arrays.stream(processorTypes)
                .forEach(processorType -> toProcessorBean.accept(processorType, processors));
        return processors;
    }

    private BiConsumer<Class<Processor>, List<Processor>> toProcessorBean = (processorType, processors) ->
            processors.add(this.processorFactory.getProcessor(generateServiceLocatorLookupBeanName(processorType)));
}
