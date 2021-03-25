package io.sapl.generator.structured;

import io.sapl.generator.GeneralConfiguration;
import lombok.Data;

@Data
public class StructuredRandomConfiguration extends GeneralConfiguration {

//    String name = "";
//    private long seed = 0L;

    boolean cleanPolicyDirectory = true;
    //    long seed = 2454325;

    // #### DOMAIN ####
    int numberOfSubjects = 100;
    int limitOfSubjectRoles = 5;
    int numberOfLockedSubjects = 0;

    // actions
    int numberOfActions = 150;

    // resources
    int numberOfGeneralResources = 300;
    double probabilityOfExtendedResource = 0.4D;
    double probabilityOfUnrestrictedResource = 0.1D;

    // roles
    int numberOfGeneralRoles = 30;
    int numberOfRolesPerSubject = 1;
    double probabilityOfExtendedRole = 0.6D;
    double probabilityOfGeneralFullAccessRole = 0.1D;
    double probabilityOfGeneralReadAccessRole = 0.1D;
    double probabilityOfGeneralCustomAccessRole = 0.2D;

    // per resource & role
    double probabilityFullAccessOnResource = 0.2D;
    double probabilityReadAccessOnResource = 0.3D;
    double probabilityCustomAccessOnResource = 0.8D;

    // AuthorizationSubscription Generation
    int numberOfBenchmarkRuns = 300;
    int subscriptionGenerationFactor = 5;
    double probabilityEmptySubscription = 0.95D;
    double probabilityEmptySubscriptionNode = 0.8D;

}
