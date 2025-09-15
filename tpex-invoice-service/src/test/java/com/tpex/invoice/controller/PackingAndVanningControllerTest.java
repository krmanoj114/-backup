package com.tpex.invoice.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.tpex.invoice.dto.CustomLabelResponseDTO;
import com.tpex.invoice.dto.PackingAndVanningResponseDTO;
import com.tpex.invoice.dto.PackingVanningReportResponseDTO;
import com.tpex.invoice.dto.SsLineGroupBoxResponseDTO;
import com.tpex.invoice.dto.VanningPlantResponseDTO;
import com.tpex.invoice.service.PackingAndVanningService;

class PackingAndVanningControllerTest {

	@InjectMocks
    private PackingAndVanningController packingAndVanningController;

    @Mock
    private PackingAndVanningService packingAndVanningService;

    @BeforeEach
     void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
     void testGetPackingAndVanningResponseData() {
        String companyCode = "companyCode";
        
        // Mock the behavior of packingAndVanningService.getPackingAndVanningResponseData()
        PackingAndVanningResponseDTO mockResponseDTO = new PackingAndVanningResponseDTO(/* Initialize DTO here */);
        when(packingAndVanningService.getPackingAndVanningResponseData(companyCode))
            .thenReturn(mockResponseDTO);
        
        // Call the controller method
        ResponseEntity<PackingAndVanningResponseDTO> response = packingAndVanningController.getPackingAndVanningResponseData(companyCode);
        
        // Assertions
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockResponseDTO, response.getBody());

        // Verify that the service method was called with the correct parameter
        verify(packingAndVanningService).getPackingAndVanningResponseData(companyCode);
        // Optionally, you can also use verifyNoMoreInteractions to ensure no unexpected interactions
        verifyNoMoreInteractions(packingAndVanningService);
    }
    
    @Test
     void testGetVanningPlantResponseData() {
        String companyCode = "companyCode";
        String planningFlag = "planningFlag";
        
        // Mock the behavior of packingAndVanningService.getVanningPlantResponseData()
        VanningPlantResponseDTO mockResponseDTO = new VanningPlantResponseDTO(/* Initialize DTO here */);
        when(packingAndVanningService.getVanningPlantResponseData(companyCode, planningFlag))
            .thenReturn(mockResponseDTO);
        
        // Call the controller method
        ResponseEntity<VanningPlantResponseDTO> response = packingAndVanningController.getVanningPlantResponseData(companyCode, planningFlag);
        
        // Assertions
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockResponseDTO, response.getBody());

        // Verify that the service method was called with the correct parameters
        verify(packingAndVanningService).getVanningPlantResponseData(companyCode, planningFlag);
        // Optionally, you can also use verifyNoMoreInteractions to ensure no unexpected interactions
        verifyNoMoreInteractions(packingAndVanningService);
    }
    
    @Test
     void testGetCustomLabelResponseData() {
        String companyCode = "companyCode";
        
        // Mock the behavior of packingAndVanningService.getCustomLabelResponseData()
        CustomLabelResponseDTO mockResponseDTO = new CustomLabelResponseDTO(/* Initialize DTO here */);
        when(packingAndVanningService.getCustomLabelResponseData(companyCode))
            .thenReturn(mockResponseDTO);
        
        // Call the controller method
        ResponseEntity<CustomLabelResponseDTO> response = packingAndVanningController.getCustomLabelResponseData(companyCode);
        
        // Assertions
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockResponseDTO, response.getBody());

        // Verify that the service method was called with the correct parameter
        verify(packingAndVanningService).getCustomLabelResponseData(companyCode);
        // Optionally, you can also use verifyNoMoreInteractions to ensure no unexpected interactions
        verifyNoMoreInteractions(packingAndVanningService);
    }
    
    @Test
     void testGetSsLineGroupBoxResponseData() {
        // Mock the behavior of packingAndVanningService.getSsLineGroupBoxResponseData()
        SsLineGroupBoxResponseDTO mockResponseDTO = new SsLineGroupBoxResponseDTO(/* Initialize DTO here */);
        when(packingAndVanningService.getSsLineGroupBoxResponseData())
            .thenReturn(mockResponseDTO);
        
        // Call the controller method
        ResponseEntity<SsLineGroupBoxResponseDTO> response = packingAndVanningController.getSsLineGroupBoxResponseData();
        
        // Assertions
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockResponseDTO, response.getBody());

        // Verify that the service method was called
        verify(packingAndVanningService).getSsLineGroupBoxResponseData();
        // Optionally, you can also use verifyNoMoreInteractions to ensure no unexpected interactions
        verifyNoMoreInteractions(packingAndVanningService);
    }
    
    @Test
     void testGetPackingVanningReport() throws IOException {
        // Mock the behavior of packingAndVanningService.getPackingVanningReport()
        PackingVanningReportResponseDTO mockResponseDTO = new PackingVanningReportResponseDTO(/* Initialize DTO here */);
        when(packingAndVanningService.getPackingVanningReport())
            .thenReturn(mockResponseDTO);
        
        // Call the controller method
        ResponseEntity<PackingVanningReportResponseDTO> response = packingAndVanningController.getPackingVanningReport();
        
        // Assertions
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockResponseDTO, response.getBody());

        // Verify that the service method was called
        verify(packingAndVanningService).getPackingVanningReport();
        // Optionally, you can also use verifyNoMoreInteractions to ensure no unexpected interactions
        verifyNoMoreInteractions(packingAndVanningService);
    }
}


