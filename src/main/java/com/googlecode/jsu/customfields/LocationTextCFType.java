package com.googlecode.jsu.customfields;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.customfields.impl.FieldValidationException;
import com.atlassian.jira.issue.customfields.impl.RenderableTextCFType;
import com.atlassian.jira.issue.customfields.manager.GenericConfigManager;
import com.atlassian.jira.issue.customfields.persistence.CustomFieldValuePersister;
import com.atlassian.jira.issue.fields.TextFieldCharacterLengthValidator;
import com.atlassian.jira.util.I18nHelper;
import com.atlassian.jira.user.ApplicationUser;

/**
 * Wrapper on Jira RenderableTextCFType for using inside plugins v2.
 *
 * @author <A href="mailto:abashev at gmail dot com">Alexey Abashev</A>
 */
//TODO Can we remove dependencies to jira-core (instead only jira-api) by using interfaces (instead of classes) of referenced custom field types?
public class LocationTextCFType extends RenderableTextCFType {
    protected final I18nHelper.BeanFactory beanFactory;

    public LocationTextCFType(
            CustomFieldValuePersister customFieldValuePersister,
            GenericConfigManager genericConfigManager,
            TextFieldCharacterLengthValidator textFieldCharacterLengthValidator,
            final I18nHelper.BeanFactory beanFactory
    ) {
        super(customFieldValuePersister, genericConfigManager, textFieldCharacterLengthValidator, ComponentAccessor.getJiraAuthenticationContext());
        this.beanFactory = beanFactory;
    }

    public String getSingularObjectFromString(final String string) throws FieldValidationException {
        if(string!=null && string.contains("=>")) {
            I18nHelper i18nh = this.beanFactory.getInstance(
                    ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser());
            String message = i18nh.getText("edit-location.invalid_location");
            throw new FieldValidationException(message);
        }
        return string;
    }
}
