package org.archivemanager.services.reporting;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class ReportingService {	
	@Autowired private List<Report> reports = new ArrayList<Report>();
	
	
	public Report findByName(String name) {
		Optional<Report> report = reports.stream().filter(r->r.getName().equals(name)).findFirst();
		if(report.isPresent()) return report.get();
		return null;
	}
	public List<Report> getReports() {
		return reports.stream().sorted((o1, o2)->o1.getName().compareTo(o2.getName())).collect(Collectors.toList());
	}
	public void setReports(List<Report> reports) {
		this.reports = reports;
	}	
}
