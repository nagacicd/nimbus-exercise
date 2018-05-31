package my.gov.practice.common.bpm.esb.listener;

import my.gov.practice.common.bpm.esb.util.AbstractContextLoader;
import my.gov.practice.common.bpm.esb.util.CommonBpmConfiguration;
import my.gov.practice.services.message.workflow.AssignmentType;
import my.gov.practice.services.message.workflow.samapleUserRequest;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.TaskListener;
import org.apache.camel.ProducerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author Naga
 
 Here is  code for  assiging task or delegate to group or individual
 */
public class CommonTaskAssigner extends AbstractContextLoader implements TaskListener {
    private static final Logger logger = LoggerFactory.getLogger(CommonTaskAssigner.class);

    private Expression branchId;
    private Expression groupId;
    private Expression assignmentType;

    @SuppressWarnings("unchecked")
	@Override
    public void notify(DelegateTask delegateTask) {
    	    	
        try {
        	
            ProducerTemplate producerTemplate = getDefaultCamelContext().createProducerTemplate();

            AssignmentType assignmentType = AssignmentType.valueOf((String) this.assignmentType.getValue(delegateTask));
            switch (assignmentType) {
                case TO_USER:
                	logger.info("CommonTaskAssigner TO_USER");
                    delegateTask.setAssignee(producerTemplate.requestBody(assigneeEndpoint, samapleUserRequest, String.class));
                    logger.info("---------------------setAssignee TO_USER-------------------");
                    break;

                case TO_CANDIDATE_USERS:
                	logger.info("CommonTaskAssigner TO_CANDIDATE_USERS");
                    delegateTask.addCandidateUsers(producerTemplate.requestBody(candidateUsersEndpoint, samapleUserRequest, List.class));
                    logger.info("---------------------addCandidateUsers TO_CANDIDATE_USERS-------------------");
                    break;

                case TO_CANDIDATE_GROUPS:
                    delegateTask.addCandidateGroup(String.valueOf(this.branchId.getValue(delegateTask) + "-" + this.groupId.getValue(delegateTask)));
                    logger.info("---------------------addCandidateGroup TO_CANDIDATE_GROUPS-------------------");
                    break;
            }
        } catch (Exception e) {
            logger.error("Common Task Assignser Error during sending request.", e);
        }
    }
}
