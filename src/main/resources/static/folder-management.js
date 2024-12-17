document.addEventListener('DOMContentLoaded', getFolders);
const btn = document.querySelector("button");
const closeSpan = document.getElementsByClassName("close")[0];

function showFiles(folderId) {
    fetch(`/folders/details/${folderId}`)
        .then(response => response.json())
        .then(item => {
            document.getElementById("currentPath").textContent = item.path;
            const fileDisplay = document.getElementById("file-display");
            fileDisplay.innerHTML = "";
            var folderParent = document.getElementById('folder-child-'+folderId);
            folderParent.style.background = folderId == 0 ? 'none' : 'red';
            item.children.forEach(file => {
                const row = document.createElement("tr");
                const updatedOn = file.modified ? new Date(file.modified).toLocaleDateString('en-GB') : "N/A";
                const createdDate = file.created ? new Date(file.created).toLocaleDateString('en-GB') : "N/A";
                row.innerHTML = `
                <td onclick="showFiles(${file.id})"><i class="fa fa-folder" aria-hidden="true"></i> ${file.name}</td>
                <td>${updatedOn}</td>
                <td>${createdDate}</td>
                <td><button class="btn-sm btn-danger" onclick="editFolder('${file.name}', ${file.id})">Change folder</button></td>
            `;
                fileDisplay.appendChild(row);
            });
            item.files.forEach(file => {
                const row = document.createElement("tr");
                const updatedOn = file.updated ? new Date(file.updated).toLocaleDateString('en-GB') : "N/A";
                const createdDate = file.created ? new Date(file.created).toLocaleDateString('en-GB') : "N/A";
                row.id = 'file-'+file.id;
                row.innerHTML = `
                <td onclick="showIdFile(${file.id})"><i class="fa fa-file" aria-hidden="true"></i> ${file.name}</td>
                <td>${updatedOn}</td>
                <td>${createdDate}</td>
                <td></td>
            `;
                fileDisplay.appendChild(row);
            });
            document.getElementById("idParentOld").value = document.getElementById("idParent").value;
            document.getElementById("idParent").value = folderId;
            if(document.getElementById("idParentOld").value){
                var folderParentOld = document.getElementById('folder-child-'+document.getElementById("idParentOld").value);
                folderParentOld.style.background = 'none';
            }
        })
        .catch(error => console.error('Error:', error));
}

function showIdFile(idFile) {
    document.getElementById("idFileOld").value = document.getElementById("idFile").value;
    document.getElementById("idFile").value = idFile;
    var fileElement = document.getElementById('file-'+ idFile);
    fileElement.style.background = 'green';
    if(document.getElementById("idFileOld").value){
        var fileElementOld = document.getElementById('file-'+document.getElementById("idFileOld").value);
        if(fileElementOld){
            fileElementOld.style.background = 'none';
        }

    }
}

function renderTree(data, container) {
    data.forEach(item => {
        const li = document.createElement('li');
        const node = document.createElement('span');
        node.className = 'folder';
        node.id = 'folder-child-'+item.id;
        node.innerHTML = `
                ${item.children && item.children.length > 0
            ? `<i class="fa fa-caret-right" id="folder-dir-${item.id}" onclick="toggleVisibility(${item.id})"></i>`
            : ''}
                <i class="fa fa-folder" onclick="showFiles(${item.id})"></i>
                ${item.name}
            `;
        li.appendChild(node);
        if (item.children && item.children.length > 0) {
            const ul = document.createElement('ul');
            ul.id = 'folder-' + item.id;
            ul.style.display = 'none';
            renderTree(item.children, ul);
            li.appendChild(ul);
        }
        container.appendChild(li);
    });
}

function toggleVisibility(folderId) {
    var folder = document.getElementById('folder-'+folderId);
    if (folder) {
        var dir = document.getElementById('folder-dir-'+folderId);
        if (folder.style.display === "none") {
            folder.style.display = "block";
            folder.classList.add("active");
            dir.classList.remove("fa-angle-right");
            dir.classList.add("fa-angle-down");
        } else {
            folder.style.display = "none";
            folder.classList.remove("active");
            dir.classList.add("fa-angle-right");
            dir.classList.remove("fa-angle-down");
        }
    }
}

var preItems = [];

function getFolders(){
    fetch(`/folders/getFolder`)
        .then(response => response.json())
        .then(items => {
            preItems = items;
            const treeElement = document.getElementById("tree");
            treeElement.innerHTML = "";
            renderTree(items, treeElement);
        })
        .catch(error => console.error('Error:', error));
}

function openModal() {
    const modal = document.getElementById("addFolderModal");
    modal.style.display = "block";
}

function closeModal() {
    const modal = document.getElementById("addFolderModal");
    modal.style.display = "none";
}

async function addFolder(){
    var name = document.getElementById("folderName").value;
    var id = document.getElementById("folderId").value;
    if(id){
        getEditFolder(name, id);
    }else{
        if(!name){
            alert('Name is not null');
            return;
        }
        if(preItems.filter(x=>x.name == name).length > 0){
            alert('Name is exit');
            return;
        }
        var response = await fetch('/folders/createFolder' + `?idFolder=${document.getElementById("idParent").value}&name=${encodeURIComponent(name)}`, {
            method: "POST",
        });
        document.getElementById("folderName").value = '';
        closeModal();
        getFolders();
        showFiles(document.getElementById("idParent").value);
    }

}

async function getEditFolder(name, id){
    var name = document.getElementById("folderName").value;
    var id = document.getElementById("folderId").value;
    if(!name){
        alert('Name is not null');
        return;
    }
    var response = await fetch('/folders/updateFolder' + `?idFolder=${id}&name=${encodeURIComponent(name)}`, {
        method: "POST",
    });
    document.getElementById("folderName").value = '';
    document.getElementById("folderId").value = '';
    showFiles(document.getElementById("idParent").value);
    getFolders()
    closeModal();
    document.getElementById("label-folder").textContent = 'Add folder';
    document.getElementById("btn-save-folder").textContent = 'Add Folder';
}

function editFolder(name, id){
    document.getElementById("label-folder").textContent = 'Change name folder';
    document.getElementById("btn-save-folder").textContent = 'Change folder';
    document.getElementById("folderName").value = name;
    document.getElementById("folderId").value = id;
    openModal();
}

function uploadFile(){
    const fileInput = document.getElementById("fileInput");
    fileInput.click();
}

async function onSelectFile() {
    const fileInput = document.getElementById("fileInput");
    const files = fileInput.files;
    if (!files || files.length === 0) {
        alert("No files selected!");
        return;
    }
    const formData = new FormData();
    formData.append("folderId", document.getElementById("idParent").value);
    for (let i = 0; i < files.length; i++) {
        formData.append("files[]", files[i]);
    }
    try {
        const response = await fetch("/file/uploadFiles", {
            method: "POST",
            body: formData,
        });
        if (response.ok) {
            showFiles(document.getElementById("idParent").value);
        } else {
            alert("lỗi update");
        }
    } catch (error) {

    }
}

async function backFolder(){
    var id = document.getElementById("idParent").value;
    if(id){
        fetch(`/folders/details/${id}`)
            .then(response => response.json())
            .then(item => {
                showFiles(item.idFolder)
            })
            .catch(error => console.error('Error:', error));
    }
}

async function downloadFile() {
    var idFile = document.getElementById("idFile").value;
    if (idFile) {
        try {
            const response = await fetch(`/file/downloadFile/${idFile}`);
            if (response.ok) {
                const fileResponse = await response.json();
                const blob = new Blob([new Uint8Array(fileResponse.fileData)], { type: fileResponse.type });
                const link = document.createElement("a");
                link.href = window.URL.createObjectURL(blob);
                link.download = fileResponse.name; // Đặt tên file cho việc tải
                link.click();
                window.URL.revokeObjectURL(link.href);
            } else {
                alert("Lỗi khi tải tệp");
            }
        } catch (error) {
            console.error("Có lỗi xảy ra:", error);
            alert("Lỗi khi tải tệp");
        }
    }
}

async function deleteFolderOrFile() {
    var idFile = document.getElementById("idFile").value;
    var idFolder = document.getElementById("idParent").value;
    if (idFile || idFolder) {
        try {
            const response = await fetch(idFile ? `/file/delete/${idFile}` : `/folders/delete/${idFolder}`, {
                method: "POST",
            });
            if (response.ok) {
                if (idFile){
                    document.getElementById("idFile").value = null;
                    showFiles(idFolder);
                } else {
                    document.getElementById("idParent").value = null;
                    getFolders();
                }
            } else {
                alert("Lỗi xóa file");
            }
        } catch (error) {
            console.error("Có lỗi xảy ra:", error);
            alert("Lỗi khi tải tệp");
        }
    }
}