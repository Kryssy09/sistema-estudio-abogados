package pe.com.mesadepartes.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChartPoint {
    private String label;
    private Long value;
}