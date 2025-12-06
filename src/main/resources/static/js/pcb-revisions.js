// ========== GESTIÓN DE REVISIONES PCB ==========

// Variables globales
let currentDesignId = null;
let currentDesignName = '';
let currentRevisions = [];
let csvData = null;
let selectedRevisionId = null;

// ========== MODAL PRINCIPAL: REVISIONES ==========
async function openRevisionsModal(designId, designName) {
    currentDesignId = designId;
    currentDesignName = designName;
    
    document.getElementById('revisions-modal-title').textContent = `Revisiones: ${designName}`;
    document.getElementById('revisions-modal').style.display = 'block';
    
    await loadRevisions();
    switchRevisionTab('list');
}

function closeRevisionsModal() {
    document.getElementById('revisions-modal').style.display = 'none';
    currentDesignId = null;
    currentRevisions = [];
}

// ========== TABS ==========
function switchRevisionTab(tabName) {
    // Ocultar todos los tabs
    document.querySelectorAll('.tab-content').forEach(tab => {
        tab.classList.remove('active');
    });
    document.querySelectorAll('.tab-button').forEach(btn => {
        btn.classList.remove('active');
    });
    
    // Mostrar tab seleccionado
    document.getElementById(`tab-${tabName}`).classList.add('active');
    event.currentTarget.classList.add('active');
    
    if (tabName === 'bom') {
        populateRevisionSelector();
    }
}

// ========== CARGAR REVISIONES ==========
async function loadRevisions() {
    try {
        currentRevisions = await apiGet(`/api/pcb-designs/${currentDesignId}/revisions`);
        renderRevisions();
    } catch (error) {
        console.error('Error al cargar revisiones:', error);
        showNotification('Error al cargar revisiones', 'error');
    }
}

function renderRevisions() {
    const container = document.getElementById('revisions-list');
    
    if (currentRevisions.length === 0) {
        container.innerHTML = `
            <div style="padding: 30px; text-align: center; color: #636e72;">
                <p style="font-size: 48px; margin: 0;">📋</p>
                <p style="font-size: 18px; margin: 10px 0;">No hay revisiones aún</p>
                <p style="font-size: 14px;">Crea la primera revisión para comenzar</p>
            </div>
        `;
        return;
    }
    
    container.innerHTML = currentRevisions.map(rev => {
        const badge = getLifecycleBadge(rev.lifecycle);
        const lockIcon = rev.locked ? '<span class="locked-icon" title="Aprobado y bloqueado">🔒</span>' : '';
        const specs = buildSpecsSummary(rev);
        
        return `
            <div class="revision-item">
                <div class="revision-info">
                    <div class="revision-code">
                        ${rev.revisionCode} ${badge} ${lockIcon}
                    </div>
                    <div class="revision-specs">${specs}</div>
                    <div class="revision-specs">
                        📦 BOM: ${rev.bomEntriesCount || 0} manual + ${rev.bomImportEntriesCount || 0} importados
                        ${rev.unmatchedComponentsCount > 0 ? 
                          `<span style="color: #d63031;"> | ⚠️ ${rev.unmatchedComponentsCount} sin vincular</span>` : ''}
                    </div>
                    ${rev.approvedBy ? 
                      `<div class="revision-specs">✅ Aprobado por ${rev.approvedBy} el ${formatDate(rev.approvedAt)}</div>` : ''}
                </div>
                <div class="revision-actions">
                    <button onclick="viewRevisionDetails(${rev.id})" class="btn btn-sm btn-info" title="Ver detalles">
                        👁️ Ver
                    </button>
                    ${!rev.locked ? `
                        <button onclick="openEditRevisionModal(${rev.id})" class="btn btn-sm btn-warning" title="Editar">
                            ✏️
                        </button>
                        <button onclick="approveRevision(${rev.id})" class="btn btn-sm btn-success" title="Aprobar">
                            ✅ Aprobar
                        </button>
                    ` : ''}
                    ${rev.locked && rev.lifecycle === 'PROTOTYPE' ? `
                        <button onclick="promoteToProduction(${rev.id})" class="btn btn-sm btn-success" title="Promover a Producción">
                            🚀 Producción
                        </button>
                    ` : ''}
                    ${rev.lifecycle === 'PRODUCTION' ? `
                        <button onclick="deprecateRevision(${rev.id})" class="btn btn-sm btn-secondary" title="Deprecar">
                            📦 Deprecar
                        </button>
                    ` : ''}
                    ${!rev.locked && hasRole('ADMIN') ? `
                        <button onclick="deleteRevision(${rev.id})" class="btn btn-sm btn-danger" title="Eliminar">
                            🗑️
                        </button>
                    ` : ''}
                </div>
            </div>
        `;
    }).join('');
}

function buildSpecsSummary(rev) {
    const parts = [];
    if (rev.width && rev.height) parts.push(`${rev.width}×${rev.height}mm`);
    if (rev.layers) parts.push(`${rev.layers} capas`);
    if (rev.thickness) parts.push(`${rev.thickness}mm espesor`);
    if (rev.finish) parts.push(formatEnumValue(rev.finish));
    if (rev.maskColor) parts.push(`máscara ${formatEnumValue(rev.maskColor).toLowerCase()}`);
    return parts.join(' | ') || 'Sin especificaciones';
}

function getLifecycleBadge(lifecycle) {
    const badges = {
        'DRAFT': '<span class="revision-badge badge-draft">BORRADOR</span>',
        'PROTOTYPE': '<span class="revision-badge badge-prototype">PROTOTIPO</span>',
        'PRODUCTION': '<span class="revision-badge badge-production">PRODUCCIÓN</span>',
        'DEPRECATED': '<span class="revision-badge badge-deprecated">DEPRECADO</span>'
    };
    return badges[lifecycle] || '';
}

function formatEnumValue(value) {
    if (!value) return '';
    return value.replace(/_/g, ' ')
               .split(' ')
               .map(w => w.charAt(0) + w.slice(1).toLowerCase())
               .join(' ');
}

// ========== CREAR/EDITAR REVISIÓN ==========
function openCreateRevisionModal() {
    document.getElementById('revision-form-title').textContent = 'Nueva Revisión';
    document.getElementById('revision-form').reset();
    document.getElementById('revision-form').dataset.revisionId = '';
    document.getElementById('revision-form-modal').style.display = 'block';
}

async function openEditRevisionModal(revisionId) {
    try {
        const revision = await apiGet(`/api/pcb-revisions/${revisionId}`);
        
        document.getElementById('revision-form-title').textContent = 'Editar Revisión';
        document.getElementById('revision-code').value = revision.revisionCode || '';
        document.getElementById('width').value = revision.width || '';
        document.getElementById('height').value = revision.height || '';
        document.getElementById('layers').value = revision.layers || '';
        document.getElementById('thickness').value = revision.thickness || '';
        document.getElementById('finish').value = revision.finish || '';
        document.getElementById('mask-color').value = revision.maskColor || '';
        document.getElementById('material').value = revision.material || '';
        document.getElementById('technical-notes').value = revision.technicalNotes || '';
        
        document.getElementById('revision-form').dataset.revisionId = revisionId;
        document.getElementById('revision-form-modal').style.display = 'block';
    } catch (error) {
        console.error('Error:', error);
        showNotification('Error al cargar revisión', 'error');
    }
}

function closeRevisionFormModal() {
    document.getElementById('revision-form-modal').style.display = 'none';
}

async function handleRevisionSubmit(event) {
    event.preventDefault();
    
    const formData = {
        revisionCode: document.getElementById('revision-code').value.trim(),
        width: parseFloat(document.getElementById('width').value) || null,
        height: parseFloat(document.getElementById('height').value) || null,
        layers: parseInt(document.getElementById('layers').value) || null,
        thickness: parseFloat(document.getElementById('thickness').value) || null,
        finish: document.getElementById('finish').value || null,
        maskColor: document.getElementById('mask-color').value || null,
        material: document.getElementById('material').value.trim() || null,
        technicalNotes: document.getElementById('technical-notes').value.trim() || null
    };
    
    const revisionId = document.getElementById('revision-form').dataset.revisionId;
    
    try {
        if (revisionId) {
            await apiPut(`/api/pcb-revisions/${revisionId}`, formData);
            showNotification('Revisión actualizada exitosamente', 'success');
        } else {
            await apiPost(`/api/pcb-designs/${currentDesignId}/revisions`, formData);
            showNotification('Revisión creada exitosamente', 'success');
        }
        
        closeRevisionFormModal();
        await loadRevisions();
    } catch (error) {
        console.error('Error:', error);
        showNotification(error.message || 'Error al guardar revisión', 'error');
    }
}

// ========== ACCIONES DE REVISIÓN ==========
async function approveRevision(revisionId) {
    if (!confirm('¿Aprobar esta revisión? Una vez aprobada no podrá ser editada.')) return;
    
    try {
        await apiPost(`/api/pcb-revisions/${revisionId}/approve`);
        showNotification('Revisión aprobada y bloqueada exitosamente', 'success');
        await loadRevisions();
    } catch (error) {
        console.error('Error:', error);
        showNotification(error.message || 'Error al aprobar revisión', 'error');
    }
}

async function promoteToProduction(revisionId) {
    if (!confirm('¿Promover esta revisión a PRODUCCIÓN? Esta acción marca la revisión como la versión oficial para manufactura.')) return;
    
    try {
        await apiPost(`/api/pcb-revisions/${revisionId}/promote-to-production`);
        showNotification('Revisión promovida a PRODUCCIÓN exitosamente', 'success');
        await loadRevisions();
    } catch (error) {
        console.error('Error:', error);
        showNotification(error.message || 'Error al promover revisión', 'error');
    }
}

async function deprecateRevision(revisionId) {
    if (!confirm('¿Deprecar esta revisión? Se marcará como obsoleta pero se mantendrá en el historial.')) return;
    
    try {
        await apiPost(`/api/pcb-revisions/${revisionId}/deprecate`);
        showNotification('Revisión marcada como DEPRECADA', 'success');
        await loadRevisions();
    } catch (error) {
        console.error('Error:', error);
        showNotification(error.message || 'Error al deprecar revisión', 'error');
    }
}

async function deleteRevision(revisionId) {
    if (!confirm('¿Eliminar esta revisión? Esta acción no se puede deshacer.')) return;
    
    try {
        await apiDelete(`/api/pcb-revisions/${revisionId}`);
        showNotification('Revisión eliminada exitosamente', 'success');
        await loadRevisions();
    } catch (error) {
        console.error('Error:', error);
        showNotification(error.message || 'Error al eliminar revisión', 'error');
    }
}

// ========== DETALLES DE REVISIÓN ==========
async function viewRevisionDetails(revisionId) {
    try {
        const revision = await apiGet(`/api/pcb-revisions/${revisionId}`);
        
        const specsHtml = `
            <div class="specs-grid">
                ${revision.width ? `<div class="spec-item">
                    <div class="spec-label">Ancho</div>
                    <div class="spec-value">${revision.width} mm</div>
                </div>` : ''}
                ${revision.height ? `<div class="spec-item">
                    <div class="spec-label">Alto</div>
                    <div class="spec-value">${revision.height} mm</div>
                </div>` : ''}
                ${revision.layers ? `<div class="spec-item">
                    <div class="spec-label">Capas</div>
                    <div class="spec-value">${revision.layers}</div>
                </div>` : ''}
                ${revision.thickness ? `<div class="spec-item">
                    <div class="spec-label">Espesor</div>
                    <div class="spec-value">${revision.thickness} mm</div>
                </div>` : ''}
                ${revision.finish ? `<div class="spec-item">
                    <div class="spec-label">Acabado Superficial</div>
                    <div class="spec-value">${formatEnumValue(revision.finish)}</div>
                </div>` : ''}
                ${revision.maskColor ? `<div class="spec-item">
                    <div class="spec-label">Color Máscara</div>
                    <div class="spec-value">${formatEnumValue(revision.maskColor)}</div>
                </div>` : ''}
                ${revision.material ? `<div class="spec-item">
                    <div class="spec-label">Material</div>
                    <div class="spec-value">${revision.material}</div>
                </div>` : ''}
            </div>
            ${revision.technicalNotes ? `
                <div style="margin-top: 20px;">
                    <h4>Notas Técnicas</h4>
                    <p style="background: #f8f9fa; padding: 15px; border-radius: 6px; border-left: 3px solid var(--primary-color);">
                        ${revision.technicalNotes}
                    </p>
                </div>
            ` : ''}
            <div style="margin-top: 20px;">
                <h4>Bill of Materials</h4>
                <p>📦 ${revision.bomEntriesCount || 0} componentes manuales</p>
                <p>📥 ${revision.bomImportEntriesCount || 0} componentes importados</p>
                ${revision.unmatchedComponentsCount > 0 ? 
                  `<p style="color: #d63031;">⚠️ ${revision.unmatchedComponentsCount} componentes sin vincular al inventario</p>` : ''}
            </div>
        `;
        
        document.getElementById('revision-details-title').textContent = `Revisión ${revision.revisionCode}`;
        document.getElementById('revision-details-content').innerHTML = specsHtml;
        document.getElementById('revision-details-modal').style.display = 'block';
    } catch (error) {
        console.error('Error:', error);
        showNotification('Error al cargar detalles', 'error');
    }
}

function closeRevisionDetailsModal() {
    document.getElementById('revision-details-modal').style.display = 'none';
}

// ========== IMPORTACIÓN DE BOM (CSV) ==========
function populateRevisionSelector() {
    const selector = document.getElementById('revision-selector');
    selector.innerHTML = '<option value="">-- Seleccionar revisión --</option>' +
        currentRevisions.map(rev => 
            `<option value="${rev.id}">${rev.revisionCode} (${rev.lifecycle})</option>`
        ).join('');
}

// Drag and Drop para CSV
const dropArea = document.getElementById('csv-drop-area');
const fileInput = document.getElementById('csv-file-input');

dropArea.addEventListener('click', () => fileInput.click());

dropArea.addEventListener('dragover', (e) => {
    e.preventDefault();
    dropArea.classList.add('dragover');
});

dropArea.addEventListener('dragleave', () => {
    dropArea.classList.remove('dragover');
});

dropArea.addEventListener('drop', (e) => {
    e.preventDefault();
    dropArea.classList.remove('dragover');
    
    const files = e.dataTransfer.files;
    if (files.length > 0) {
        handleCsvFile({ target: { files } });
    }
});

function handleCsvFile(event) {
    const file = event.target.files[0];
    if (!file) return;
    
    if (!file.name.endsWith('.csv')) {
        showNotification('Por favor selecciona un archivo CSV', 'error');
        return;
    }
    
    const reader = new FileReader();
    reader.onload = (e) => {
        const content = e.target.result;
        parseAndPreviewCsv(content);
    };
    reader.readAsText(file);
}

function parseAndPreviewCsv(csvContent) {
    const lines = csvContent.split('\n').filter(line => line.trim());
    if (lines.length < 2) {
        showNotification('El archivo CSV está vacío o no tiene datos', 'error');
        return;
    }
    
    // Parsear CSV (formato: Designators, MPN, Quantity, Description, Value, Footprint, Manufacturer)
    const headers = lines[0].split(',').map(h => h.trim());
    csvData = [];
    
    for (let i = 1; i < lines.length; i++) {
        const values = lines[i].split(',').map(v => v.trim());
        csvData.push({
            designators: values[0] || '',
            mpn: values[1] || '',
            quantity: parseInt(values[2]) || 1,
            description: values[3] || '',
            value: values[4] || '',
            footprint: values[5] || '',
            manufacturer: values[6] || ''
        });
    }
    
    // Mostrar preview
    const preview = document.getElementById('csv-preview');
    preview.innerHTML = `
        <strong>Archivo cargado: ${csvData.length} líneas detectadas</strong><br><br>
        <table style="width: 100%; font-size: 11px;">
            <thead>
                <tr style="background: #dfe6e9;">
                    <th style="padding: 5px;">Designators</th>
                    <th style="padding: 5px;">MPN</th>
                    <th style="padding: 5px;">Qty</th>
                    <th style="padding: 5px;">Description</th>
                </tr>
            </thead>
            <tbody>
                ${csvData.slice(0, 10).map(row => `
                    <tr>
                        <td style="padding: 5px;">${row.designators}</td>
                        <td style="padding: 5px;"><strong>${row.mpn}</strong></td>
                        <td style="padding: 5px;">${row.quantity}</td>
                        <td style="padding: 5px;">${row.description}</td>
                    </tr>
                `).join('')}
                ${csvData.length > 10 ? `<tr><td colspan="4" style="text-align: center; padding: 10px; color: #636e72;">... y ${csvData.length - 10} líneas más</td></tr>` : ''}
            </tbody>
        </table>
    `;
    
    document.getElementById('csv-preview-container').style.display = 'block';
}

async function confirmBomImport() {
    const revisionId = document.getElementById('revision-selector').value;
    if (!revisionId) {
        showNotification('Selecciona una revisión primero', 'error');
        return;
    }
    
    if (!csvData || csvData.length === 0) {
        showNotification('No hay datos CSV cargados', 'error');
        return;
    }
    
    const overwrite = document.getElementById('overwrite-bom').checked;
    const minConfidence = parseFloat(document.getElementById('min-confidence').value);
    
    const payload = {
        rows: csvData,
        overwrite: overwrite,
        minimumConfidence: minConfidence
    };
    
    try {
        const result = await apiPost(`/api/pcb-revisions/${revisionId}/import-bom`, payload);
        
        // Mostrar resultado
        const resultHtml = `
            <div style="background: ${result.success ? '#d5f4e6' : '#ffeaa7'}; padding: 20px; border-radius: 8px; border-left: 4px solid ${result.success ? '#00b894' : '#fdcb6e'};">
                <h3>${result.success ? '✅' : '⚠️'} ${result.message}</h3>
                <div style="margin-top: 15px;">
                    <p><strong>Total de filas:</strong> ${result.totalRows}</p>
                    <p><strong>Vinculados exitosamente:</strong> ${result.matchedRows} (${result.matchSuccessRate.toFixed(1)}%)</p>
                    <p><strong>No vinculados:</strong> ${result.unmatchedRows}</p>
                </div>
                
                ${result.notFoundMpns && result.notFoundMpns.length > 0 ? `
                    <div style="margin-top: 15px; padding: 10px; background: #fff; border-radius: 5px;">
                        <strong>⚠️ MPNs no encontrados en inventario:</strong>
                        <ul style="margin: 5px 0; padding-left: 20px;">
                            ${result.notFoundMpns.map(mpn => `<li>${mpn}</li>`).join('')}
                        </ul>
                    </div>
                ` : ''}
                
                ${result.insufficientStockWarnings && result.insufficientStockWarnings.length > 0 ? `
                    <div style="margin-top: 15px; padding: 10px; background: #fff; border-radius: 5px;">
                        <strong>📦 Advertencias de stock insuficiente:</strong>
                        <ul style="margin: 5px 0; padding-left: 20px;">
                            ${result.insufficientStockWarnings.map(warn => `<li>${warn}</li>`).join('')}
                        </ul>
                    </div>
                ` : ''}
                
                ${result.entries && result.entries.length > 0 ? `
                    <div style="margin-top: 20px;">
                        <h4>Detalles de vinculación:</h4>
                        <table style="width: 100%; font-size: 12px; background: white; border-radius: 5px; overflow: hidden;">
                            <thead>
                                <tr style="background: #dfe6e9;">
                                    <th style="padding: 8px;">Designators</th>
                                    <th style="padding: 8px;">MPN</th>
                                    <th style="padding: 8px;">Componente</th>
                                    <th style="padding: 8px;">Confianza</th>
                                    <th style="padding: 8px;">Stock</th>
                                </tr>
                            </thead>
                            <tbody>
                                ${result.entries.slice(0, 15).map(entry => `
                                    <tr>
                                        <td style="padding: 8px;">${entry.designators}</td>
                                        <td style="padding: 8px;"><strong>${entry.mpn}</strong></td>
                                        <td style="padding: 8px;">
                                            ${entry.matched ? 
                                              `<span class="match-indicator match-success"></span>${entry.componenteNombre}` :
                                              `<span class="match-indicator match-failed"></span>No vinculado`}
                                        </td>
                                        <td style="padding: 8px;">
                                            ${entry.matched ? `
                                                <div class="confidence-bar">
                                                    <div class="confidence-fill" style="width: ${entry.matchConfidence * 100}%"></div>
                                                </div>
                                                ${(entry.matchConfidence * 100).toFixed(0)}%
                                            ` : '-'}
                                        </td>
                                        <td style="padding: 8px;">
                                            ${entry.matched ? 
                                              (entry.stockSufficient ? 
                                                `✅ ${entry.stockActual}` : 
                                                `⚠️ ${entry.stockActual}`) :
                                              '-'}
                                        </td>
                                    </tr>
                                `).join('')}
                                ${result.entries.length > 15 ? 
                                  `<tr><td colspan="5" style="text-align: center; padding: 10px; color: #636e72;">... y ${result.entries.length - 15} filas más</td></tr>` : ''}
                            </tbody>
                        </table>
                    </div>
                ` : ''}
            </div>
        `;
        
        document.getElementById('bom-result').innerHTML = resultHtml;
        document.getElementById('bom-result').style.display = 'block';
        
        showNotification('Importación de BOM completada', 'success');
        await loadRevisions();
        
    } catch (error) {
        console.error('Error:', error);
        showNotification(error.message || 'Error al importar BOM', 'error');
    }
}

// ========== UTILIDADES ==========
function formatDate(dateString) {
    if (!dateString) return '-';
    const date = new Date(dateString);
    return date.toLocaleDateString('es-ES', { 
        year: 'numeric', 
        month: 'short', 
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });
}

function hasRole(role) {
    const userRole = localStorage.getItem('role');
    return userRole === role;
}
