package pe.com.mesadepartes.dtos.expediente;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubirArchivos {
  private List<DocItem> docs = new ArrayList<>();
}
