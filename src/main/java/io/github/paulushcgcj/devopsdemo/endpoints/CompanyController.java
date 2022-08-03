package io.github.paulushcgcj.devopsdemo.endpoints;

import io.github.paulushcgcj.devopsdemo.models.Company;
import io.github.paulushcgcj.devopsdemo.services.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
public class CompanyController {

  private final CompanyService service;

  @GetMapping
  public List<Company> listCompanies(
      @RequestParam(required = false,defaultValue = "0") long page,
      @RequestParam(required = false,defaultValue = "10") long size,
      @RequestParam(required = false) String name
      ){
    return service.listCompanies(page, size,name);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public void addCompany(@RequestBody @Valid Company company, HttpServletResponse response){
    String id = service.addCompany(company);
    response.addHeader("Location",String.format("/api/companies/%s",id));
  }

  @GetMapping("/{id}")
  public Company getCompany(@PathVariable String id){
    return service.getCompany(id);
  }

  @PutMapping("/{id}")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public void updateCompany(@PathVariable String id,@RequestBody Company company){
    service.updateCompany(id,company);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void removeCompany(@PathVariable String id){
    service.removeCOmpany(id);
  }
}
