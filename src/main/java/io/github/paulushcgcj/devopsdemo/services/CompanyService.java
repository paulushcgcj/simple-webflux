package io.github.paulushcgcj.devopsdemo.services;

import io.github.paulushcgcj.devopsdemo.exceptions.CompanyAlreadyExistException;
import io.github.paulushcgcj.devopsdemo.exceptions.CompanyNotFoundException;
import io.github.paulushcgcj.devopsdemo.exceptions.NullCompanyException;
import io.github.paulushcgcj.devopsdemo.models.Company;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CompanyService {

  private final Map<String, Company> companyRepository = new HashMap<>();

  public List<Company> listCompanies(long page, long size, String name) {
    return
        companyRepository
            .values()
            .stream()
            .filter(filterByName(name))
            .skip(page * size)
            .limit(size)
            .collect(Collectors.toList());
  }

  public String addCompany(Company company) {
    if (company != null && listCompanies(0, 1, company.getName()).isEmpty()) {
      company.setId(UUID.randomUUID().toString());
      companyRepository.put(company.getId(), company);
      return company.getId();
    }
    if (company == null)
      throw new NullCompanyException();
    throw new CompanyAlreadyExistException(company.getName());
  }

  public Company getCompany(String id) {
    return
        companyRepository
            .values()
            .stream()
            .filter(company -> company.getId().equalsIgnoreCase(id))
            .findFirst()
            .orElseThrow(() -> new CompanyNotFoundException(id));
  }

  public void updateCompany(String id, Company company) {
    if (company != null) {
      getCompany(id);
      company.setId(id);
      companyRepository.put(company.getId(), company);
    }
    if (company == null)
      throw new NullCompanyException();
  }

  public void removeCOmpany(String id) {
    Company company = getCompany(id);
    companyRepository.remove(company.getId());
  }

  private Predicate<Company> filterByName(String name) {
    if (StringUtils.isNotBlank(name)) {
      return company -> company
          .getName()
          .toLowerCase()
          .contains(name.toLowerCase());
    }
    return company -> true;
  }

}
