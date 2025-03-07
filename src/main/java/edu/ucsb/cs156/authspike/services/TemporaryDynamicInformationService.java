package edu.ucsb.cs156.authspike.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TemporaryDynamicInformationService {

    @Value("${app.github.installationid}")
    private String installationId;

    public String getInstallationId() {
        return installationId;
    }
}
