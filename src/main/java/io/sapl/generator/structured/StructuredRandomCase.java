package io.sapl.generator.structured;

import io.sapl.benchmark.BenchmarkCase;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StructuredRandomCase extends BenchmarkCase {

    //    String name;
    //    long seed;

    boolean cleanPolicyDirectory = true;

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

    int subscriptionGenerationFactor = 5;
    double probabilityEmptySubscription = 0.95D;
    double probabilityEmptySubscriptionNode = 0.8D;

}
