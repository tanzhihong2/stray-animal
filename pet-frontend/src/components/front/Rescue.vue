<template>
  <div class="rescue-module">
    <!-- 救助站卡片列表 -->
    <div class="rescue-cards" v-if="tableData.length > 0">
      <div class="rescue-card" v-for="(item, index) in tableData" :key="index">
        <div class="card-image">
          <el-image 
            class="station-image"
            :src="$store.state.baseApi+item.img" 
            :preview-src-list="[$store.state.baseApi + item.img]"
            fit="cover">
            <div slot="error" class="image-slot">
              <i class="el-icon-office-building"></i>
            </div>
          </el-image>
          <div class="station-badge">
            <i class="el-icon-first-aid-kit"></i>
            救助站
          </div>
        </div>
        
        <div class="card-content">
          <h3 class="station-name">
            <i class="el-icon-office-building"></i>
            {{ item.name || '未命名救助站' }}
          </h3>
          
          <div class="station-info">
            <div class="info-item">
              <i class="el-icon-location-outline"></i>
              <span class="label">地址：</span>
              <span class="value">{{ item.addres || '地址信息待完善' }}</span>
            </div>
            
            <div class="info-item">
              <i class="el-icon-user"></i>
              <span class="label">负责人：</span>
              <span class="value">{{ item.person || '待联系' }}</span>
            </div>
            
            <div class="info-item">
              <i class="el-icon-phone"></i>
              <span class="label">电话：</span>
              <span class="value">{{ item.phone || '暂无' }}</span>
            </div>
            
            <div class="info-item description" v-if="item.information">
              <i class="el-icon-document"></i>
              <span class="label">描述：</span>
              <span class="value">{{ item.information }}</span>
            </div>
          </div>
        </div>
        
        <div class="card-actions">
          <el-button 
            type="primary" 
            size="small" 
            @click="contactStation(item)"
            class="contact-btn">
            <i class="el-icon-phone"></i>
            联系救助
          </el-button>
          <el-button 
            type="text" 
            size="small" 
            @click="viewDetails(item)"
            class="detail-btn">
            <i class="el-icon-view"></i>
            查看详情
          </el-button>
        </div>
      </div>
    </div>
    
    <!-- 空状态 -->
    <div class="empty-rescue" v-if="tableData.length === 0">
      <div class="empty-icon">
        <i class="el-icon-office-building"></i>
      </div>
      <h4>暂无救助站信息</h4>
      <p>正在努力建设更多的救助站点，请稍后再来查看</p>
    </div>

    <!-- 分页器 -->
    <div class="pagination-container" v-if="tableData.length > 0">
      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="pageNum"
        :page-sizes="[6, 12, 24]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next"
        :total="total"
        background
        small>
      </el-pagination>
    </div>

    <!-- 救助站详情对话框 -->
    <el-dialog 
      :title="dialogTitle" 
      :visible.sync="dialogFormVisible" 
      width="500px" 
      :close-on-click-modal="false"
      class="rescue-dialog">
      <el-form 
        :model="form" 
        label-width="100px" 
        class="rescue-form">
        <el-form-item label="站点名称">
          <el-input v-model="form.name" autocomplete="off" placeholder="请输入救助站名称"></el-input>
        </el-form-item>
        <el-form-item label="站点地址">
          <el-input v-model="form.addres" autocomplete="off" placeholder="请输入详细地址"></el-input>
        </el-form-item>
        <el-form-item label="站点照片">
          <el-upload 
            :action="$store.state.baseApi+'/file/upload'" 
            ref="img" 
            :on-success="handleImgUploadSuccess"
            list-type="picture-card"
            :limit="1">
            <i class="el-icon-plus"></i>
          </el-upload>
        </el-form-item>
        <el-form-item label="负责人">
          <el-input v-model="form.person" autocomplete="off" placeholder="请输入负责人姓名"></el-input>
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="form.phone" autocomplete="off" placeholder="请输入联系电话"></el-input>
        </el-form-item>
        <el-form-item label="相关描述">
          <el-input 
            v-model="form.information" 
            type="textarea"
            :rows="3"
            placeholder="请输入救助站的相关介绍和描述"
            autocomplete="off">
          </el-input>
        </el-form-item>
      </el-form>
      
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false" class="cancel-btn">
          <i class="el-icon-close"></i>
          取消
        </el-button>
        <el-button type="primary" @click="save" class="save-btn">
          <i class="el-icon-check"></i>
          保存
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: "Rescue",
  data() {
    return {
      tableData: [],
      total: 0,
      pageNum: 1,
      pageSize: 6,
      name: "",
      form: {},
      dialogFormVisible: false,
      dialogTitle: '救助站信息',
      multipleSelection: [],
      user: localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : {}
    }
  },
  created() {
    this.load()
  },
  methods: {
    load() {
      this.request.get("/rescue/page", {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          name: this.name,
        }
      }).then(res => {
        this.tableData = res.data.records
        this.total = res.data.total
      })
    },
    save() {
      this.request.post("/rescue", this.form).then(res => {
        if (res.code === '200') {
          this.$message.success("保存成功")
          this.dialogFormVisible = false
          this.load()
        } else {
          this.$message.error("保存失败")
        }
      })
    },
    contactStation(item) {
      if (item.phone) {
        this.$confirm(`联系 ${item.person || '负责人'}：${item.phone}`, '联系救助站', {
          confirmButtonText: '拨打电话',
          cancelButtonText: '取消',
          type: 'info'
        }).then(() => {
          window.open(`tel:${item.phone}`)
        })
      } else {
        this.$message.warning('该救助站暂无联系电话')
      }
    },
    viewDetails(item) {
      this.form = JSON.parse(JSON.stringify(item))
      this.dialogTitle = `${item.name || '救助站'} - 详细信息`
      this.dialogFormVisible = true
      this.$nextTick(() => {
        if(this.$refs.img) {
          this.$refs.img.clearFiles();
        }
      })
    },
    handleAdd() {
      this.dialogFormVisible = true
      this.form = {}
      this.$nextTick(() => {
        if(this.$refs.img) {
           this.$refs.img.clearFiles();
         }
         if(this.$refs.file) {
          this.$refs.file.clearFiles();
         }
      })
    },
    handleEdit(row) {
      this.form = JSON.parse(JSON.stringify(row))
      this.dialogFormVisible = true
       this.$nextTick(() => {
         if(this.$refs.img) {
           this.$refs.img.clearFiles();
         }
         if(this.$refs.file) {
          this.$refs.file.clearFiles();
         }
       })
    },
    del(id) {
      this.request.delete("/rescue/" + id).then(res => {
        if (res.code === '200') {
          this.$message.success("删除成功")
          this.load()
        } else {
          this.$message.error("删除失败")
        }
      })
    },
    handleSelectionChange(val) {
      console.log(val)
      this.multipleSelection = val
    },
    delBatch() {
      if (!this.multipleSelection.length) {
        this.$message.error("请选择需要删除的数据")
        return
      }
      let ids = this.multipleSelection.map(v => v.id)  // [{}, {}, {}] => [1,2,3]
      this.request.post("/rescue/del/batch", ids).then(res => {
        if (res.code === '200') {
          this.$message.success("批量删除成功")
          this.load()
        } else {
          this.$message.error("批量删除失败")
        }
      })
    },
    reset() {
      this.name = ""
      this.load()
    },
    handleSizeChange(pageSize) {
      console.log(pageSize)
      this.pageSize = pageSize
      this.load()
    },
    handleCurrentChange(pageNum) {
      console.log(pageNum)
      this.pageNum = pageNum
      this.load()
    },
    handleFileUploadSuccess(res) {
      this.form.file = res
    },
    handleImgUploadSuccess(res) {
      this.form.img = res
    },
    download(url) {
      window.open(url)
    },
    exp() {
      window.open(this.$store.state.baseApi+"/rescue/export")
    },
    handleExcelImportSuccess() {
      this.$message.success("导入成功")
      this.load()
    }
  }
}
</script>


<style scoped>
.rescue-module {
  margin: 20px 0;
}

/* 救助站卡片布局 */
.rescue-cards {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 25px;
  margin-bottom: 30px;
}

.rescue-card {
  background: white;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  border: 1px solid #f0f0f0;
  position: relative;
}

.rescue-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 15px 40px rgba(0, 0, 0, 0.15);
}

/* 图片区域 */
.card-image {
  position: relative;
  height: 200px;
  overflow: hidden;
}

.station-image {
  width: 100%;
  height: 100%;
}

.image-slot {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  font-size: 40px;
}

.station-badge {
  position: absolute;
  top: 15px;
  right: 15px;
  background: linear-gradient(135deg, #ff6b6b 0%, #ee5a24 100%);
  color: white;
  padding: 8px 15px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 5px;
  box-shadow: 0 4px 15px rgba(255, 107, 107, 0.3);
}

/* 卡片内容 */
.card-content {
  padding: 25px;
}

.station-name {
  font-size: 20px;
  font-weight: 700;
  color: #2c3e50;
  margin: 0 0 20px 0;
  display: flex;
  align-items: center;
  gap: 10px;
}

.station-name i {
  color: #667eea;
  font-size: 22px;
}

.station-info {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.info-item {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  font-size: 14px;
  line-height: 1.5;
}

.info-item i {
  color: #667eea;
  font-size: 16px;
  margin-top: 2px;
  flex-shrink: 0;
}

.info-item .label {
  font-weight: 600;
  color: #34495e;
  min-width: 60px;
}

.info-item .value {
  color: #7f8c8d;
  flex: 1;
}

.info-item.description {
  align-items: flex-start;
}

.info-item.description .value {
  line-height: 1.6;
}

/* 卡片操作 */
.card-actions {
  display: flex;
  justify-content: space-between;
  padding: 0 25px 25px;
  gap: 15px;
}

.contact-btn {
  background: linear-gradient(135deg, #00b894 0%, #00a085 100%);
  border: none;
  border-radius: 20px;
  padding: 10px 20px;
  font-weight: 600;
  color: white;
  transition: all 0.3s ease;
  flex: 1;
}

.contact-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0, 184, 148, 0.4);
}

.detail-btn {
  color: #667eea !important;
  font-weight: 600;
  border-radius: 20px;
  padding: 10px 15px;
}

.detail-btn:hover {
  background: rgba(102, 126, 234, 0.1) !important;
  color: #5a67d8 !important;
}

/* 空状态 */
.empty-rescue {
  text-align: center;
  padding: 60px 20px;
  background: white;
  border-radius: 20px;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
}

.empty-rescue .empty-icon {
  font-size: 60px;
  color: #bdc3c7;
  margin-bottom: 20px;
}

.empty-rescue h4 {
  color: #2c3e50;
  margin-bottom: 10px;
  font-size: 18px;
}

.empty-rescue p {
  color: #7f8c8d;
  margin: 0;
}

/* 分页器 */
.pagination-container {
  display: flex;
  justify-content: center;
  padding: 25px;
  background: white;
  border-radius: 20px;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
}

/* 对话框样式 */
.rescue-dialog .el-dialog {
  border-radius: 20px;
  overflow: hidden;
}

.rescue-dialog .el-dialog__header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 25px 30px;
}

.rescue-dialog .el-dialog__title {
  color: white;
  font-weight: 600;
}

.rescue-form {
  padding: 20px;
}

.rescue-form .el-form-item__label {
  font-weight: 600;
  color: #2c3e50;
}

.rescue-form .el-input__inner,
.rescue-form .el-textarea__inner {
  border-radius: 10px;
  border: 2px solid #e9ecef;
  transition: all 0.3s ease;
}

.rescue-form .el-input__inner:focus,
.rescue-form .el-textarea__inner:focus {
  border-color: #667eea;
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.1);
}

.dialog-footer {
  display: flex;
  justify-content: center;
  gap: 20px;
  padding: 25px;
}

.cancel-btn {
  padding: 10px 25px;
  border-radius: 20px;
  border: 2px solid #e74c3c;
  color: #e74c3c;
  background: white;
  font-weight: 600;
}

.cancel-btn:hover {
  background: #e74c3c;
  color: white;
}

.save-btn {
  padding: 10px 25px;
  border-radius: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  font-weight: 600;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .rescue-cards {
    grid-template-columns: 1fr;
  }
  
  .card-actions {
    flex-direction: column;
  }
}

/* 动画效果 */
@keyframes slideInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.rescue-card {
  animation: slideInUp 0.6s ease forwards;
}

.rescue-card:nth-child(even) {
  animation-delay: 0.1s;
}

.rescue-card:nth-child(odd) {
  animation-delay: 0.2s;
}
</style>
