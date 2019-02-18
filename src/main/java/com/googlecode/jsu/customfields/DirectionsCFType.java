package com.googlecode.jsu.customfields;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.customfields.impl.FieldValidationException;
import com.atlassian.jira.issue.customfields.impl.RenderableTextCFType;
import com.atlassian.jira.issue.customfields.manager.GenericConfigManager;
import com.atlassian.jira.issue.customfields.persistence.CustomFieldValuePersister;
import com.atlassian.jira.util.I18nHelper;
import com.atlassian.jira.issue.fields.TextFieldCharacterLengthValidator;
//import com.atlassian.crowd.embedded.api.User;
import org.apache.log4j.Logger;
/**
 * Google Directions custom field, uses =&gt; as separator between origin
 * and destination location.
 */
public class DirectionsCFType extends RenderableTextCFType {
	
    protected final I18nHelper.BeanFactory beanFactory;

    public DirectionsCFType(CustomFieldValuePersister customFieldValuePersister,
                            GenericConfigManager genericConfigManager,
                            TextFieldCharacterLengthValidator textFieldCharacterLengthValidator,
                            final I18nHelper.BeanFactory beanFactory) {
    	
        super(customFieldValuePersister, genericConfigManager, textFieldCharacterLengthValidator, ComponentAccessor.getJiraAuthenticationContext());
        this.beanFactory = beanFactory;
    }

    public String getSingularObjectFromString(final String string) throws FieldValidationException {
        if(string!=null && !string.contains("=>")) {
            I18nHelper i18nh = this.beanFactory.getInstance(
                    ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser());
            String message = i18nh.getText("edit-directions.invalid_directions");
            throw new FieldValidationException(message);
        }
        return string;
    }
}