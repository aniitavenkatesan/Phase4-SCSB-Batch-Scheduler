package org.recap.batch.job;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCase;
import org.recap.ScsbConstants;
import org.recap.batch.service.AccessionReconcilationService;
import org.springframework.batch.core.*;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by Anitha V on 14/7/20.
 */

public class AccessionReconcilationTaskletUT extends BaseTestCase {

    @Mock
    AccessionReconcilationService accessionReconcilationService;

    @Value("${scsb.circ.url}")
    String scsbCircUrl;

    @Mock
    AccessionReconcilationTasklet accessionReconcilationTasklet;

    @Test
    public void testexecute_Exception() throws Exception {
        StepContribution contribution = new StepContribution(new StepExecution("StatusReconcilationStep", new JobExecution(new JobInstance(123L, "job"),new JobParameters())));
        StepExecution execution = MetaDataInstanceFactory.createStepExecution();
        execution.setCommitCount(2);
        ChunkContext context = new ChunkContext(new StepContext(execution));
        Mockito.when(accessionReconcilationService.accessionReconcilation(scsbCircUrl)).thenThrow(new NullPointerException());
        Mockito.when(accessionReconcilationTasklet.execute(contribution,context)).thenCallRealMethod();
        RepeatStatus status = accessionReconcilationTasklet.execute(contribution,context);
        assertNotNull(status);
        assertEquals(RepeatStatus.FINISHED,status);
    }

    @Test
    public void testexecute() throws Exception {
        StepContribution contribution = new StepContribution(new StepExecution("StatusReconcilationStep", new JobExecution(new JobInstance(25L, "PeriodicLASItemStatusReconciliation"),new JobParameters())));
        StepExecution execution = MetaDataInstanceFactory.createStepExecution();
        execution.setCommitCount(2);
        ChunkContext context = new ChunkContext(new StepContext(execution));
        ReflectionTestUtils.setField(accessionReconcilationTasklet,"accessionReconcilationService",accessionReconcilationService);
        Mockito.when(accessionReconcilationService.accessionReconcilation(scsbCircUrl)).thenReturn(ScsbConstants.SUCCESS);
        Mockito.when(accessionReconcilationTasklet.execute(contribution,context)).thenCallRealMethod();
        RepeatStatus status = accessionReconcilationTasklet.execute(contribution,context);
        assertNotNull(status);
        assertEquals(RepeatStatus.FINISHED,status);
    }


}
