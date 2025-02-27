<template>
    <!--添加标签弹窗-->
    <el-dialog :append-to-body="true" :title="config.title" v-model="config.visible" @close="matClose()">
        <el-form ref="formRef" :model="config.form" :rules="rules" label-width="80px">
            <el-form-item label="标签名" prop="name">
                <el-input v-model="config.form.name" clearable />
            </el-form-item>
            <el-form-item label="标签类型">
                <el-select v-model="config.form.selectValue" placeholder="请选择标签类型">
                    <el-option v-for="item in config.options" :key="item.key" :value="item.value" />
                </el-select>
            </el-form-item>
            <el-form-item label="预览" v-if="config.form.name">
                <el-tag :type="config.form.selectValue">{{ config.form.name }}</el-tag>
            </el-form-item>
            <el-form-item>
                <el-button type="primary" @click="onSubmit">确定</el-button>
                <el-button @click="onCancel">取消</el-button>
            </el-form-item>
        </el-form>
    </el-dialog>
    <div class="regional_table">

        <el-table :data="tableData" style="width: 100%;">
            <el-table-column prop="name" label="标签名" width="150" />
            <el-table-column prop="type" label="类型" width="120" />
            <el-table-column prop="num" label="已引用数" width="120" />
            <el-table-column label="预览" width="180">
                <template #default="scope">
                    <el-tag :type="scope.row.type">{{ scope.row.name }}</el-tag>
                </template>
            </el-table-column>
            <el-table-column :align="right">
                <template #header>
                    <el-row>
                        <el-col :span="4">
                            <el-select v-model="searchType" placeholder="Select">
                                <el-option label="标签名" value="1" />
                                <el-option label="类型" value="2" />
                            </el-select>
                        </el-col>
                        <el-col :span="16">
                            <el-input v-if="searchType == '1'" v-model="search" placeholder="标签名查找" @change="searchStart"
                                clearable />
                            <el-autocomplete v-else style="width: 100%;" v-model="search" :fetch-suggestions="querySearch"
                                clearable placeholder="标签类型查找" @select="searchTypeStart" />
                        </el-col>
                        <el-col :span="4">
                            <el-button circle @click="addTag">
                                <el-icon size="16px">
                                    <Plus />
                                </el-icon>
                            </el-button>
                        </el-col>
                    </el-row>
                </template>
                <template #default="scope">
                    <el-button @click="handleEdit(scope.$index, scope.row)">编辑</el-button>
                    <el-button type="danger" @click="handleDelete(scope.$index, scope.row)">删除</el-button>
                </template>
            </el-table-column>
        </el-table>
    </div>
</template>
<script>
import { deleteTag, updateTag, getTagByName, addTag, getTagByUid, getTagByType } from '@/request/api';
import { Plus } from '@element-plus/icons-vue'
export default {
    data() {
        return {
            rules: {
                //属性名和表单中的属性名保持一致，不然会验证错误
                name: [{ required: true, trigger: 'blur', message: "请输入标签名" }],
            },
            config: {
                title: "添加标签",
                visible: false,
                form: {
                    name: '',
                    selectValue: '',
                },
                options: [{ value: "primary", key: 1 },
                { value: "success", key: 2 },
                { value: "info", key: 2 },
                { value: "warning", key: 2 },
                { value: "danger", key: 2 },],
            },
            searchType: '',
            search: "",
            tableData: [],
            editId: 0,
        }
    },
    components: {
        Plus
    },
    methods: {
        querySearch(queryString, cb) {
            const results = queryString ? this.config.options.filter((item) => { return item.value.indexOf(queryString) !== -1 }
            ) : this.config.options
            cb(results)
        },
        handleEdit(index, row) {
            console.log("index:" + index);
            this.config.form.name = row.name;
            this.config.form.selectValue = row.type;
            this.editId = row.id;
            this.config.visible = true;
        },
        async handleDelete(index, row) {
            console.log("index:" + index);
            await deleteTag({
                id: row.id
            }).then(res => {
                if (res.flag == "success") {
                    alert("删除成功");
                    this.showAll();
                }
            })
        },
        searchStart() {
            if (this.searchType == 1) {
                this.showAll(this.search, '');
            } else if (this.searchType == 2) {
                this.showAll("", this.search);
            }
        },
        addTag() {
            this.config.visible = true;
        },
        matClose() {
            this.editId = 0;
            this.config.form.name = '';
            this.config.form.selectValue = this.config.options[0].value;
        },
        onCancel() {
            this.config.visible = false;
        },
        async onSubmit() {
            if (!this.$refs['formRef']) return;
            await this.$refs['formRef'].validate((valid) => {
                if (valid) {
                    try {
                        console.log(this.form);
                        var user = null;
                        if (typeof (this.$store.getters.getUser) != 'object')
                            user = JSON.parse(this.$store.getters.getUser);
                        else user = this.$store.getters.getUser;
                        if (this.editId == 0) {
                            addTag({
                                name: this.config.form.name,
                                type: this.config.form.selectValue,
                                uid: user.uid
                            }).then(res => {
                                if (res.flag == "success") {
                                    alert("添加成功");
                                    this.config.visible = false;
                                    this.showAll();
                                } else {
                                    alert(res.data);
                                }
                            })
                        } else {
                            updateTag({
                                name: this.config.form.name,
                                type: this.config.form.selectValue,
                                uid: user.uid,
                                id: this.editId
                            }).then(res => {
                                if (res.flag == "success") {
                                    alert("修改成功");
                                    this.config.visible = false;
                                    this.showAll();
                                }
                            })
                        }
                    } catch (error) {
                        console.log(error);
                    }
                } else {
                    alert("请填写所有带*的信息")
                }
            })
        },
        async showAll(name, type) {
            var user = null;
            if (typeof (this.$store.getters.getUser) != 'object')
                user = JSON.parse(this.$store.getters.getUser);
            else user = this.$store.getters.getUser;
            if (name != '' && type == '') {
                await getTagByName({
                    uid: user.uid,
                    name: name
                }).then(res => {
                    if (res.flag == "success") {
                        this.tableData = res.data;
                    }
                })
            } else if (type != '' && name == '') {
                await getTagByType({
                    uid: user.uid,
                    type: type
                }).then(res => {
                    if (res.flag == "success") {
                        this.tableData = res.data;
                    }
                })
            } else {
                await getTagByUid(user.uid).then(res => {
                    if (res.flag == "success") {
                        this.tableData = res.data;
                    }
                })
            }
        }
    },
    created() {
        this.config.form.selectValue = this.config.options[0].value;
        this.searchType = '1';
        this.showAll();
    },
}
</script>
<style lang="scss">
/* .el-table .rowStyle{
    background-color: transparent;
} */
</style>