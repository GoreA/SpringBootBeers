package guru.spring.spring7restmvc.mappers;

import guru.spring.spring7restmvc.entities.Beer;
import guru.spring.spring7restmvc.models.BeerDTO;
import org.mapstruct.Mapper;

@Mapper
public interface BeerMapper {
  Beer beerDTOtoBeer(BeerDTO beerDTO);
  BeerDTO beertoBeerDTO(Beer beer);
}
