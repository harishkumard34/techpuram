
<script lang="ts">
  import { onMount } from 'svelte';
  import { fade } from 'svelte/transition';

  let to: string = "";
  let cc: string = "";
  let bcc: string = "";
  let subject: string = "";
  let body: string = "";
  let fileAttachment: File | null = null;
  let showCC = false;
  let showBCC = false;
  let recipients: string[] = [];
  let composerMinimized = false;
  let showTemplates = false;
  let showFontSizes = false;
  let showFontFamilies = false;
  let showColors = false;
  let currentTemplateId: number | null = null;

  // Template management
  let templates: Array<{templateId: number, subject: string, body: string}> = [];

  const fontSizes = Array.from({length: 14}, (_, i) => i + 1);
  const fontFamilies = ['Verdana', 'Georgia', 'serif'];
  const colors = ['black', 'red', 'blue', 'green', 'purple'];

  function addRecipient(email: string) {
    if (email && !recipients.includes(email)) {
      recipients = [...recipients, email];
      to = "";
    }
  }

  function removeRecipient(email: string) {
    recipients = recipients.filter(r => r !== email);
  }

  function handleKeyDown(event: KeyboardEvent) {
    if (event.key === 'Enter' && to) {
      event.preventDefault();
      addRecipient(to);
    }
  }

  const handleAttachment = (event: Event) => {
    const target = event.target as HTMLInputElement;
    const files = target.files;
    if (files && files.length > 0) {
      fileAttachment = files[0];
    }
  };

  function formatText(command: string, value?: string) {
    const composerElement = document.querySelector('.compose-area') as HTMLElement;
    if (composerElement) {
      composerElement.focus();
      document.execCommand(command, false, value || "");
    }
  }

  function setFontSize(size: number) {
    formatText('fontSize', size.toString());
    showFontSizes = false;
  }

  function setFontFamily(family: string) {
    formatText('fontName', family);
    showFontFamilies = false;
  }

  function setColor(color: string) {
    formatText('foreColor', color);
    showColors = false;
  }

  async function sendEmail() {
    const emailData = {
      fromAddress: "harishkumar.d@techpuram.com",
      toAddress: to,
      ccAddress: cc,
      bccAddress: bcc,
      subject: subject,
      body: body,
      templateId: currentTemplateId,
    };

    try {
      const response = await fetch('http://localhost:8080/api/email/send', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(emailData),
      });

      if (response.ok) {
        alert("Email sent successfully!");
        resetForm();
      } else {
        const errorData = await response.json();
        alert("Error sending email: " + errorData.message);
      }
    } catch (error) {
      console.error('Error:', error);
      alert("An error occurred while sending the email.");
    }
  }

  function resetForm() {
    recipients = [];
    to = "";
    cc = "";
    bcc = "";
    subject = "";
    body = "";
    fileAttachment = null;
    currentTemplateId = null;
  }

  async function saveAsTemplate() {
    if (!subject || !body) {
      alert('Please enter both subject and body to save as template');
      return;
    }

    try {
      const endpoint = currentTemplateId 
        ? `http://localhost:8080/api/templates/${currentTemplateId}`
        : 'http://localhost:8080/api/templates';
      
      const method = currentTemplateId ? 'PUT' : 'POST';

      const response = await fetch(endpoint, {
        method: method,
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          subject,
          body
        })
      });

      if (response.ok) {
        const result = await response.json();
        if (currentTemplateId) {
          templates = templates.map(t => 
            t.templateId === currentTemplateId ? result : t
          );
          alert('Template updated successfully!');
        } else {
          templates = [...templates, result];
          alert('Template saved successfully!');
        }
      }
    } catch (error) {
      console.error('Error saving template:', error);
      alert('Failed to save template');
    }
  }

  function loadTemplate(template: {templateId: number, subject: string, body: string}) {
    currentTemplateId = template.templateId;
    subject = template.subject;
    body = template.body;
    showTemplates = false;
  }

  async function updateCurrentTemplate() {
    if (!currentTemplateId) {
      alert('Please load a template first before updating');
      return;
    }

    if (!subject || !body) {
      alert('Please enter both subject and body to update template');
      return;
    }

    try {
      const response = await fetch(`http://localhost:8080/api/templates/${currentTemplateId}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          subject,
          body
        })
      });

      if (response.ok) {
        const updatedTemplate = await response.json();
        templates = templates.map(t => 
          t.templateId === currentTemplateId ? updatedTemplate : t
        );
        alert('Template updated successfully!');
      }
    } catch (error) {
      console.error('Error updating template:', error);
      alert('Failed to update template');
    }
  }

  onMount(async () => {
    try {
      const response = await fetch('http://localhost:8080/api/templates');
      if (response.ok) {
        templates = await response.json();
      }
    } catch (error) {
      console.error('Error fetching templates:', error);
    }
  });
</script>

<div class="compose-window" class:minimized={composerMinimized}>
  <div class="compose-header">
    <div class="sender-info">
      <span>Techpuram &lt;harishkumar.d@techpuram.com&gt;</span>
    </div>
    <div class="header-actions">
      <button class="template-button" on:click={() => showTemplates = !showTemplates}>
        Insert Template
        <span class="chevron-down">▼</span>
      </button>
      <button class="minimize-button" on:click={() => composerMinimized = !composerMinimized}>
        {composerMinimized ? '□' : '−'}
      </button>
    </div>
  </div>

  <div class="compose-body" class:hidden={composerMinimized}>
    <div class="recipients-field">
      <span class="field-label">To</span>
      <div class="recipients-list">
        {#each recipients as recipient}
          <div class="recipient-chip" transition:fade>
            {recipient}
            <button class="remove-recipient" on:click={() => removeRecipient(recipient)}>×</button>
          </div>
        {/each}
        <input
          type="email"
          bind:value={to}
          on:keydown={handleKeyDown}
          placeholder="Recipients"
        />
      </div>
      <div class="recipient-actions">
        <button class="cc-button" class:active={showCC} on:click={() => showCC = !showCC}>Cc</button>
        <button class="cc-button" class:active={showBCC} on:click={() => showBCC = !showBCC}>Bcc</button>
      </div>
    </div>

    {#if showCC}
      <div class="input-row">
        <span class="field-label">Cc</span>
        <input type="email" bind:value={cc} placeholder="Carbon copy recipients" />
      </div>
    {/if}

    {#if showBCC}
      <div class="input-row">
        <span class="field-label">Bcc</span>
        <input type="email" bind:value={bcc} placeholder="Blind carbon copy recipients" />
      </div>
    {/if}

    <div class="input-row">
      <input type="text" bind:value={subject} placeholder="Subject" />
    </div>

    {#if showTemplates}
      <div class="templates-dropdown">
        {#if templates.length === 0}
          <p class="no-templates">No saved templates</p>
        {:else}
          {#each templates as template}
            <button 
              class="template-item"
              class:active={template.templateId === currentTemplateId}
              on:click={() => loadTemplate(template)}
            >
              {template.subject}
            </button>
          {/each}
        {/if}
      </div>
    {/if}

    <div class="editor-toolbar">
      <button title="Bold" on:click={() => formatText("bold")}><b>B</b></button>
      <button title="Italic" on:click={() => formatText("italic")}><i>I</i></button>
      <button title="Underline" on:click={() => formatText("underline")}><u>U</u></button>
      <span class="separator">|</span>
      <div class="font-controls">
        <button class="font-button" on:click={() => showFontSizes = !showFontSizes}>N</button>
        {#if showFontSizes}
          <div class="dropdown-menu">
            {#each fontSizes as size}
              <button on:click={() => setFontSize(size)}>{size}</button>
            {/each}
          </div>
        {/if}
        
        <button class="font-button" on:click={() => showFontFamilies = !showFontFamilies}>A</button>
        {#if showFontFamilies}
          <div class="dropdown-menu">
            {#each fontFamilies as family}
              <button on:click={() => setFontFamily(family)}>{family}</button>
            {/each}
          </div>
        {/if}
        
        <button class="font-button" on:click={() => showColors = !showColors}>T</button>
        {#if showColors}
          <div class="dropdown-menu">
            {#each colors as color}
              <button 
                on:click={() => setColor(color)}
                class="color-option"
                style="background-color: {color}">
              </button>
            {/each}
          </div>
        {/if}
      </div>
    </div>

    <div class="compose-area" contenteditable="true" bind:innerHTML={body}></div>

    <div class="compose-footer">
      <div class="left-actions">
        <button class="send-button" on:click={sendEmail}>Send</button>
        <label class="attachment-button">
          📎
          <input type="file" hidden on:change={handleAttachment} />
        </label>
        <button class="save-button" on:click={saveAsTemplate}>Save</button>
        <button 
          class="update-button" 
          on:click={updateCurrentTemplate}
          disabled={!currentTemplateId}
          title={currentTemplateId ? "Update current template" : "Load a template first"}
        >
          G
        </button>
      </div>
      {#if fileAttachment}
        <div class="file-info">
          📄 {fileAttachment.name} ({Math.round(fileAttachment.size / 1024)}KB)
        </div>
      {/if}
    </div>
  </div>
</div>

<style>
  .compose-window {
    position: fixed;
    bottom: 0;
    right: 24px;
    width: 510px;
    background: white;
    border-radius: 8px 8px 0 0;
    box-shadow: 0 2px 6px rgba(0,0,0,0.15);
    font-family: 'Google Sans', Arial, sans-serif;
  }

  .compose-window.minimized {
    width: 260px;
  }

  .compose-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 8px 16px;
    background: #f2f6fc;
    border-radius: 8px 8px 0 0;
    border-bottom: 1px solid #dadce0;
  }

  .sender-info {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 14px;
    color: #202124;
  }

  .header-actions {
    display: flex;
    gap: 8px;
  }

  .template-button {
    display: flex;
    align-items: center;
    gap: 4px;
    padding: 6px 12px;
    border: 1px solid #dadce0;
    border-radius: 4px;
    background: white;
    font-size: 14px;
    color: #444746;
    cursor: pointer;
  }

  .chevron-down {
    font-size: 10px;
  }

  .minimize-button {
    padding: 4px 8px;
    background: transparent;
    border: none;
    cursor: pointer;
    font-size: 18px;
    color: #444746;
  }

  .compose-body {
    padding: 16px;
  }

  .hidden {
    display: none;
  }

  .recipients-field {
    display: flex;
    align-items: flex-start;
    gap: 8px;
    margin-bottom: 8px;
  }

  .field-label {
    color: #5f6368;
    font-size: 14px;
    padding-top: 8px;
    width: 40px;
  }

  .recipients-list {
    flex: 1;
    display: flex;
    flex-wrap: wrap;
    gap: 4px;
    min-height: 32px;
    padding: 4px 0;
    border-bottom: 1px solid #dadce0;
  }

  .recipient-chip {
    display: flex;
    align-items: center;
    gap: 4px;
    padding: 2px 8px;
    background: #e8f0fe;
    border-radius: 12px;
    font-size: 14px;
    color: #1967d2;
  }

  .remove-recipient {
    border: none;
    background: transparent;
    color: #5f6368;
    cursor: pointer;
    padding: 0 2px;
  }

  .recipients-list input {
    border: none;
    outline: none;
    flex: 1;
    min-width: 100px;
    font-size: 14px;
    padding: 4px 0;
  }

  .recipient-actions {
    display: flex;
    gap: 4px;
  }

  .cc-button {
    padding: 4px 8px;
    border: none;
    background: transparent;
    color: #5f6368;
    cursor: pointer;
    font-size: 14px;
  }

  .cc-button.active {
    color: #1a73e8;
  }

  .input-row {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-bottom: 8px;
  }

  .input-row input {
    flex: 1;
    border: none;
    border-bottom: 1px solid #dadce0;
    outline: none;
    padding: 8px 0;
    font-size: 14px;
  }

  .editor-toolbar {
    display: flex;
    align-items: center;
    gap: 4px;
    padding: 8px 0;
    border-bottom: 1px solid #dadce0;
  }

  .editor-toolbar button {
    padding: 4px 8px;
    border: none;
    background: transparent;
    color: #444746;
    cursor: pointer;
  }

  .separator {
    color: #dadce0;
    margin: 0 4px;
  }

  .compose-area {
    min-height: 320px;
    padding: 16px 0;
    outline: none;
    font-size: 14px;
    line-height: 1.5;
  }

  .compose-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding-top: 16px;
    border-top: 1px solid #dadce0;
  }

  .left-actions {
    display: flex;
    gap: 8px;
    align-items: center;
  }

  .send-button {
    padding: 8px 24px;
    background: #1a73e8;
    color: white;
    border: none;
    border-radius: 4px;
    font-size: 14px;
    font-weight: 500;
    cursor: pointer;
  }

  .save-button {
    padding: 8px 16px;
    background: #f1f3f4;
    color: #444746;
    border: none;
    border-radius: 4px;
    font-size: 14px;
    cursor: pointer;
  }

  .attachment-button {
    padding: 8px;
    cursor: pointer;
    color: #444746;
  }

  .file-info {
    font-size: 12px;
    color: #5f6368;
  }

  .templates-dropdown {
    position: absolute;
    z-index: 10;
    background: white;
    border: 1px solid #dadce0;
    border-radius: 4px;
    box-shadow: 0 2px 6px rgba(0,0,0,0.1);
    max-height: 300px;
    overflow-y: auto;
    width: calc(100% - 2rem);
    margin-top: 0.25rem;
  }

  .template-item {
    width: 100%;
    text-align: left;
    padding: 0.75rem 1rem;
    border: none;
    background: none;
    cursor: pointer;
    border-bottom: 1px solid #f1f3f4;
  }

  .template-item:hover {
    background: #f8f9fa;
  }

  .no-templates {
    padding: 1rem;
    text-align: center;
    color: #5f6368;
  }

  .font-controls {
    display: flex;
    gap: 4px;
    position: relative;
  }

  .font-button {
    padding: 4px 8px;
    border: 1px solid #dadce0;
    border-radius: 4px;
    background: white;
    color: #444746;
    cursor: pointer;
  }

  .dropdown-menu {
    position: absolute;
    top: 100%;
    left: 0;
    background: white;
    border: 1px solid #dadce0;
    border-radius: 4px;
    box-shadow: 0 2px 6px rgba(0,0,0,0.1);
    z-index: 1000;
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(30px, 1fr));
    gap: 4px;
    padding: 4px;
    min-width: 120px;
  }

  .dropdown-menu button {
    padding: 4px 8px;
    border: 1px solid #dadce0;
    border-radius: 4px;
    background: white;
    cursor: pointer;
  }

  .color-option {
    width: 24px;
    height: 24px;
    border-radius: 50%;
    border: 1px solid #dadce0;
  }
  
</style> 