package com.ehei.rdv.Mapper;

import com.ehei.rdv.DTO.RdvCreateRequestDto;
import com.ehei.rdv.DTO.RdvResponseDto;
import com.ehei.rdv.DTO.RdvUpdateRequestDto;
import com.ehei.rdv.Model.Rdv;
import org.junit.jupiter.api.*;
import java.util.List;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class RdvMapperTest {

    private RdvMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new RdvMapper();
    }
    @Test
    void RdvCreateRequestDtoToRdvTest() {
        RdvCreateRequestDto dto = RdvCreateRequestDto.builder()
                .idPatient(10L)
                .idMedecin(20L)
                .dateRdv(LocalDateTime.of(2026, 2, 20, 14, 30))
                .build();

         Rdv rdv = mapper.toModel(dto);

        assertNotNull(rdv);
        assertEquals(10L, rdv.getIdPatient());
        assertEquals(20L, rdv.getIdMedecin());
        assertEquals(LocalDateTime.of(2026, 2, 20, 14, 30), rdv.getDateRdv());
    }

    @Test
    void RdvUpdateRequestDtoToRdvTest() {
        RdvUpdateRequestDto dto = RdvUpdateRequestDto.builder()
                .idPatient(11L)
                .idMedecin(22L)
                .dateRdv(LocalDateTime.of(2026, 3, 1, 9, 0))
                .build();

        Rdv rdv = mapper.toModel(dto);

        assertNotNull(rdv);
        assertEquals(11L, rdv.getIdPatient());
        assertEquals(22L, rdv.getIdMedecin());
        assertEquals(LocalDateTime.of(2026, 3, 1, 9, 0), rdv.getDateRdv());
    }
    @Test
    void RdvToRdvResponseDtoTest()
    {
        Rdv rdv = Rdv.builder()
                .id(5L)
                .idPatient(10L)
                .idMedecin(20L)
                .dateRdv(LocalDateTime.of(2026, 2, 20, 14, 30))
                .build();

        RdvResponseDto res = mapper.toResponseDto(rdv);

        assertNotNull(res);
        assertEquals(5L, res.getId());
        assertEquals(10L, res.getIdPatient());
        assertEquals(20L, res.getIdMedecin());
        assertEquals(LocalDateTime.of(2026, 2, 20, 14, 30), res.getDateRdv());

    }

    @Test
    void ListOfRdvToListOfResponseDto() {
        List<Rdv> rdvs = List.of(
                Rdv.builder().id(1L).idPatient(10L).idMedecin(20L)
                        .dateRdv(LocalDateTime.of(2026, 2, 20, 14, 30)).build(),
                Rdv.builder().id(2L).idPatient(11L).idMedecin(21L)
                        .dateRdv(LocalDateTime.of(2026, 2, 21, 10, 0)).build()
        );

        List<RdvResponseDto> result = mapper.toListeResponseDto(rdvs);

        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals(1L, result.get(0).getId());
        assertEquals(10L, result.get(0).getIdPatient());
        assertEquals(20L, result.get(0).getIdMedecin());

        assertEquals(2L, result.get(1).getId());
        assertEquals(11L, result.get(1).getIdPatient());
        assertEquals(21L, result.get(1).getIdMedecin());
    }

    @AfterEach
    void tearDown()
    {
        mapper = null;
    }


}