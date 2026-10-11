package com.ficsolution.roster.scheduling;

import com.ficsolution.roster.service.ShiftService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ShiftCompletionJobTest {

    @InjectMocks
    private ShiftCompletionJob shiftCompletionJob;

    @Mock
    private ShiftService shiftService;

    @Test
    void mustCompletePastShifts() {
        when(shiftService.completePastShifts()).thenReturn(2);

        shiftCompletionJob.updateToCompletedShift();

        verify(shiftService, times(1)).completePastShifts();
        verifyNoMoreInteractions(shiftService);
    }
}
