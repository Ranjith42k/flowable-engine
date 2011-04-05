/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.activiti.explorer.ui.content;

import org.activiti.engine.ProcessEngines;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Attachment;
import org.activiti.explorer.ExplorerApp;
import org.activiti.explorer.I18nManager;
import org.activiti.explorer.Messages;

import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.ui.Form;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;


/**
 * @author Frederik Heremans
 */
public class UrlAttachmentEditor extends Form implements AttachmentEditor {

  private static final long serialVersionUID = 1L;
  
  protected Attachment attachment;
  protected String taskId;
  protected String processInstanceId;
  
  protected I18nManager i18nManager;
  protected TaskService taskService;
  
  public UrlAttachmentEditor(String taskId, String processInstanceId) {
    this(null, taskId, processInstanceId);
  }
  
  public UrlAttachmentEditor(Attachment attachment, String taskId, String processInstanceId) {
    this.attachment = attachment;
    this.taskId = taskId;
    this.processInstanceId = processInstanceId;
    
    this.i18nManager = ExplorerApp.get().getI18nManager();
    taskService = ProcessEngines.getDefaultProcessEngine().getTaskService();
    
    setSizeFull();
    setDescription(i18nManager.getMessage(Messages.RELATED_CONTENT_TYPE_URL_HELP));
    
    initName();
    initDescription();
    initUrl();
  }

  protected void initUrl() {
    TextField urlField = new TextField(i18nManager.getMessage(Messages.RELATED_CONTENT_TYPE_URL_URL));
    urlField.setRequired(true);
    urlField.setRequiredError(i18nManager.getMessage(Messages.RELATED_CONTENT_TYPE_URL_URL_REQUIRED));
    urlField.setWidth(100, UNITS_PERCENTAGE);
    // URL isn't mutable once attachment is created
    if(attachment != null) {
      urlField.setEnabled(false);
    }
    
    addField("url", urlField);
  }

  protected void initDescription() {
    TextArea descriptionField = new TextArea(i18nManager.getMessage(Messages.RELATED_CONTENT_DESCRIPTION));
    descriptionField.setWidth(100, UNITS_PERCENTAGE);
    descriptionField.setHeight(100, UNITS_PIXELS);
    addField("description", descriptionField);
  }

  protected void initName() {
    TextField nameField = new TextField(i18nManager.getMessage(Messages.RELATED_CONTENT_NAME));
    nameField.setRequired(true);
    nameField.setRequiredError(i18nManager.getMessage(Messages.RELATED_CONTENT_NAME_REQUIRED));
    nameField.setWidth(100, UNITS_PERCENTAGE);
    addField("name", nameField);
  }

  public Attachment getAttachment() throws InvalidValueException {
    // Force validation of the fields
    commit();
    if(attachment != null) {
      applyValuesToAttachment();
    } else {
      // Create new attachment based on values
      // TODO: use ecplorerApp to get service
      attachment = taskService.createAttachment(UrlAttachmentRenderer.ATTACHMENT_TYPE, taskId, processInstanceId, 
          getAttachmentName(), getAttachmentDescription(), getAttachmentUrl());
    }
    return attachment;
  }
  
  protected String getAttachmentUrl() {
    return (String) getField("url").getValue();
  }
  
  protected String getAttachmentName() {
    return (String) getField("name").getValue();
  }
  
  protected String getAttachmentDescription() {
    return (String) getField("description").getValue();
  }
  
  private void applyValuesToAttachment() {
    attachment.setName(getAttachmentName());
    attachment.setDescription(getAttachmentDescription());
  }
}
