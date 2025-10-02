<template>
  <div class="forum-container">
    <!-- 页面头部区域 -->
    <div class="forum-header">
      <div class="header-content">
        <div class="header-left">
          <div class="header-icon">
            <i class="el-icon-chat-dot-round"></i>
          </div>
          <div class="header-text">
            <h1>宠物论坛</h1>
            <p>分享经验，交流心得，共建温馨宠物社区</p>
          </div>
        </div>
        <div class="header-stats">
          <div class="stat-item">
            <div class="stat-number">{{ total }}</div>
            <div class="stat-label">帖子总数</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">{{ onlineUsers || 168 }}</div>
            <div class="stat-label">在线用户</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">{{ todayPosts || 23 }}</div>
            <div class="stat-label">今日发帖</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 搜索和操作区域 -->
    <div class="search-section">
      <div class="search-container">
        <div class="search-box">
          <el-input
            v-model="name"
            placeholder="搜索帖子标题或内容..."
            prefix-icon="el-icon-search"
            size="large"
            class="search-input"
            @keyup.enter.native="load"
            clearable
          >
          </el-input>
          <el-button 
            type="primary" 
            size="large" 
            @click="load" 
            class="search-btn"
            icon="el-icon-search"
          >
            搜索
          </el-button>
        </div>
        <div class="action-buttons">
          <el-button 
            type="success" 
            size="large" 
            @click="handleAdd" 
            class="post-btn"
            icon="el-icon-edit"
          >
            发表帖子
          </el-button>
        </div>
      </div>
    </div>

    <!-- 帖子列表区域 -->
    <div class="posts-section">
      <div class="posts-container">
        <div 
          v-for="item in tableData" 
          :key="item.id"
          class="post-card"
          @click="view(item.id, item.content)"
        >
          <div class="post-header">
            <div class="post-avatar">
              <el-avatar 
                :size="45" 
                :src="getAvatarUrl(item.userId)"
                icon="el-icon-user-solid"
              ></el-avatar>
            </div>
            <div class="post-info">
              <div class="post-author">{{ item.user }}</div>
              <div class="post-time">
                <i class="el-icon-time"></i>
                {{ formatTime(item.time) }}
              </div>
            </div>
            <div class="post-actions" @click.stop>
              <el-dropdown trigger="click" v-if="item.userId === user.id">
                <el-button type="text" icon="el-icon-more"></el-button>
                <el-dropdown-menu slot="dropdown">
                  <el-dropdown-item @click.native="handleEdit(item)">
                    <i class="el-icon-edit"></i> 编辑
                  </el-dropdown-item>
                  <el-dropdown-item @click.native="del(item.id)">
                    <i class="el-icon-delete"></i> 删除
                  </el-dropdown-item>
                </el-dropdown-menu>
              </el-dropdown>
            </div>
          </div>
          
          <div class="post-content">
            <h3 class="post-title">{{ item.name }}</h3>
            <p class="post-preview">{{ getContentPreview(item.content) }}</p>
          </div>
          
          <div class="post-footer">
            <div class="post-tags">
              <el-tag size="mini" type="info">{{ getPostCategory(item.name) }}</el-tag>
            </div>
            <div class="post-stats">
              <span class="stat-item">
                <i class="el-icon-view"></i>
                {{ item.views || 0 }}
              </span>
              <span class="stat-item">
                <i class="el-icon-chat-dot-round"></i>
                {{ item.comments || 0 }}
              </span>
              <el-button 
                type="text" 
                @click.stop="handleComment(item.id)"
                class="comment-btn"
              >
                <i class="el-icon-chat-line-round"></i>
                评论
              </el-button>
            </div>
          </div>
        </div>
        
        <!-- 空状态 -->
        <div v-if="!tableData.length" class="empty-state">
          <div class="empty-icon">
            <i class="el-icon-chat-dot-round"></i>
          </div>
          <div class="empty-text">
            <h3>暂无帖子</h3>
            <p>快来发表第一个帖子吧！</p>
          </div>
          <el-button type="primary" @click="handleAdd" icon="el-icon-edit">
            发表帖子
          </el-button>
        </div>
      </div>
    </div>

    <!-- 分页区域 -->
    <div class="pagination-section">
      <div class="pagination-container">
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="pageNum"
          :page-sizes="[5, 10, 20, 50]"
          :page-size="pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          background
        >
        </el-pagination>
      </div>
    </div>

    <!-- 发帖对话框 -->
    <el-dialog 
      title="发表帖子" 
      :visible.sync="dialogFormVisible" 
      width="70%" 
      :close-on-click-modal="false"
      class="post-dialog"
    >
      <el-form label-width="80px" class="post-form">
        <el-form-item label="帖子标题">
          <el-input 
            v-model="form.name" 
            autocomplete="off" 
            placeholder="请输入一个吸引人的标题..."
            maxlength="100"
            show-word-limit
          ></el-input>
        </el-form-item>
        <el-form-item label="帖子内容">
          <mavon-editor 
            ref="md" 
            v-model="form.content" 
            :ishljs="true" 
            @imgAdd="imgAdd"
            height="400px"
            placeholder="在这里分享你的想法和经验..."
          />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false" size="large">取消</el-button>
        <el-button type="primary" @click="save" size="large" icon="el-icon-check">发布</el-button>
      </div>
    </el-dialog>

    <!-- 帖子详情对话框 -->
    <el-dialog 
      :visible.sync="vis" 
      width="95%" 
      :close-on-click-modal="false"
      class="detail-dialog"
      :show-close="false"
      top="2vh"
    >
      <div slot="title" class="dialog-title">
        <div class="title-content">
          <div class="title-icon">
            <i class="el-icon-document"></i>
          </div>
          <div class="title-text">
            <h2>帖子详情</h2>
            <p>分享精彩内容，传递温暖时刻</p>
          </div>
        </div>
        <el-button @click="vis = false" class="close-btn" icon="el-icon-close" circle></el-button>
      </div>

      <div class="post-detail-container">
        <!-- 帖子内容区域 -->
        <div class="post-content-section">
          <div class="content-header">
            <div class="author-info">
              <el-avatar :size="50" icon="el-icon-user-solid" class="author-avatar"></el-avatar>
              <div class="author-details">
                <div class="author-name">{{ currentPostAuthor || '宠物爱好者' }}</div>
                <div class="post-meta">
                  <span class="meta-item">
                    <i class="el-icon-time"></i>
                    {{ formatTime(currentPostTime) }}
                  </span>
                  <span class="meta-item">
                    <i class="el-icon-view"></i>
                    {{ currentPostViews || 0 }} 阅读
                  </span>
                  <span class="meta-item">
                    <i class="el-icon-chat-dot-round"></i>
                    {{ comments.length }} 评论
                  </span>
                </div>
              </div>
            </div>
            <div class="post-actions">
              <el-button type="text" class="action-btn like-btn">
                <i class="el-icon-star-off"></i>
                点赞
              </el-button>
              <el-button type="text" class="action-btn share-btn">
                <i class="el-icon-share"></i>
                分享
              </el-button>
              <el-button type="text" class="action-btn collect-btn">
                <i class="el-icon-collection"></i>
                收藏
              </el-button>
            </div>
          </div>
          
          <div class="content-body">
            <mavon-editor
              class="md-viewer"
              :value="content"
              :subfield="false"
              :defaultOpen="'preview'"
              :toolbarsFlag="false"
              :editable="false"
              :scrollStyle="true"
              :ishljs="true"
            />
          </div>
        </div>

        <!-- 评论区域 -->
        <div class="comments-section">
          <div class="comments-header">
            <div class="header-left">
              <h3>
                <i class="el-icon-chat-dot-round"></i>
                精彩评论
              </h3>
              <span class="comments-count">{{ comments.length }} 条评论</span>
            </div>
            <el-button 
              type="primary" 
              @click="handleQuickComment" 
              class="quick-comment-btn"
              icon="el-icon-edit-outline"
            >
              写评论
            </el-button>
          </div>
          
          <!-- 快速评论框 -->
          <div class="quick-comment-form" v-if="showQuickComment">
            <div class="comment-form-header">
              <el-avatar :size="40" icon="el-icon-user-solid" class="user-avatar"></el-avatar>
              <div class="form-title">
                <h4>发表评论</h4>
                <p>分享你的想法和见解</p>
              </div>
            </div>
            <el-input 
              type="textarea" 
              v-model="quickCommentContent"
              :rows="3"
              placeholder="请文明发言，理性讨论..."
              class="comment-textarea"
              maxlength="500"
              show-word-limit
            ></el-input>
            <div class="comment-form-footer">
              <div class="form-tips">
                <i class="el-icon-info"></i>
                <span>支持Markdown语法，让你的评论更精彩</span>
              </div>
              <div class="form-actions">
                <el-button @click="showQuickComment = false">取消</el-button>
                <el-button type="primary" @click="submitQuickComment" icon="el-icon-check">
                  发表评论
                </el-button>
              </div>
            </div>
          </div>
          
          <!-- 评论列表 -->
          <div class="comments-list">
            <div 
              v-for="item in comments" 
              :key="item.id"
              class="comment-item"
            >
              <div class="comment-avatar">
                <el-avatar 
                  :size="45" 
                  :src="$store.state.baseApi + item.avatar" 
                  icon="el-icon-user-solid"
                ></el-avatar>
              </div>
              <div class="comment-content">
                <div class="comment-header">
                  <div class="comment-info">
                    <span class="comment-author">{{ item.user }}</span>
                    <span class="comment-time">
                      <i class="el-icon-time"></i>
                      {{ formatTime(item.time) }}
                    </span>
                  </div>
                  <div class="comment-actions">
                    <el-button type="text" class="reply-btn" icon="el-icon-chat-line-round">
                      回复
                    </el-button>
                    <el-button 
                      v-if="user.nickname === item.user" 
                      type="text" 
                      @click="delComment(item.id)"
                      class="delete-comment-btn"
                      icon="el-icon-delete"
                    >
                      删除
                    </el-button>
                  </div>
                </div>
                <div class="comment-text">{{ item.content }}</div>
                <div class="comment-footer">
                  <div class="comment-stats">
                    <span class="stat-item">
                      <i class="el-icon-thumb"></i>
                      {{ item.likes || 0 }}
                    </span>
                  </div>
                </div>
              </div>
            </div>
            
            <!-- 空状态 -->
            <div v-if="!comments.length" class="no-comments">
              <div class="empty-icon">
                <i class="el-icon-chat-dot-round"></i>
              </div>
              <h4>还没有评论</h4>
              <p>来发表第一条评论，分享你的想法吧！</p>
              <el-button type="primary" @click="handleQuickComment" icon="el-icon-edit-outline">
                写下第一条评论
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </el-dialog>

    <!-- 评论对话框 -->
    <el-dialog 
      title="发表评论" 
      :visible.sync="commentVis" 
      width="50%" 
      :close-on-click-modal="false"
      class="comment-dialog"
    >
      <el-form label-width="0px" class="comment-form">
        <el-form-item>
          <el-input 
            type="textarea" 
            :rows="5" 
            v-model="comment.content" 
            autocomplete="off" 
            placeholder="在这里写下你的评论..."
            maxlength="500"
            show-word-limit
          ></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="commentVis = false" size="large">取消</el-button>
        <el-button type="primary" @click="saveComment" size="large" icon="el-icon-check">发表评论</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import axios from "axios";

export default {
  name: "Article",
  data() {
    return {
      tableData: [],
      total: 0,
      pageNum: 1,
      pageSize: 10,
      name: "",
      form: {},
      dialogFormVisible: false,
      multipleSelection: [],
      user: localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : {},
      vis: false,
      content: '',
      comment: {},
      commentVis: false,
      comments: [],
      // 新增的数据属性
      onlineUsers: null,
      todayPosts: null,
      searchLoading: false,
      // 帖子详情相关数据
      currentPostAuthor: '',
      currentPostTime: '',
      currentPostViews: 0,
      showQuickComment: false,
      quickCommentContent: '',
      currentArticleId: null // 当前查看的文章ID
    }
  },
  created() {
    if (!this.user.id) {
      this.$message.warning("请登录")
      this.$router.push("/login")
      return
    }
    this.load()
  },
  methods: {
    // 新增的工具方法
    formatTime(time) {
      if (!time) return ''
      const date = new Date(time)
      const now = new Date()
      const diff = now.getTime() - date.getTime()
      
      if (diff < 60000) { // 1分钟内
        return '刚刚'
      } else if (diff < 3600000) { // 1小时内
        return Math.floor(diff / 60000) + '分钟前'
      } else if (diff < 86400000) { // 1天内
        return Math.floor(diff / 3600000) + '小时前'
      } else if (diff < 604800000) { // 1周内
        return Math.floor(diff / 86400000) + '天前'
      } else {
        return date.toLocaleDateString()
      }
    },
    
    getContentPreview(content) {
      if (!content) return '暂无内容'
      // 去除 markdown 标记
      const plainText = content.replace(/[#*`>\-\[\]()]/g, '').trim()
      return plainText.length > 120 ? plainText.substring(0, 120) + '...' : plainText
    },
    
    getPostCategory(title) {
      if (!title) return '交流讨论'
      const lowerTitle = title.toLowerCase()
      if (lowerTitle.includes('救助') || lowerTitle.includes('救治')) return '救助求助'
      if (lowerTitle.includes('领养') || lowerTitle.includes('找家')) return '领养信息'
      if (lowerTitle.includes('养护') || lowerTitle.includes('经验')) return '养护经验'
      if (lowerTitle.includes('医疗') || lowerTitle.includes('健康')) return '医疗健康'
      if (lowerTitle.includes('失踪') || lowerTitle.includes('寻找')) return '寻宠启示'
      return '交流讨论'
    },
    
    getAvatarUrl(userId) {
      // 根据用户ID生成默认头像或从用户信息中获取
      return null // 返回null使用el-avatar的默认图标
    },
    saveComment() {
      if (!this.user.username) {
        this.$message.warning("请登录后评论")
        return
      }
      if (this.comment.contentr) {
        this.comment.content = this.form.contentr
      }
      if (!this.comment.content) {
        this.$message.warning("请输入评论")
        return
      }
      this.comment.type = 2
      this.request.post("/comment", this.comment).then(res => {
        if (res.code === '200') {
          this.$message.success("评论成功")
          this.commentVis = false
          this.comment = {}  // 清空数据
          this.loadComment(this.currentArticleId)
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    delComment(id) {
      this.request.delete("/comment/" + id).then(res => {
        if (res.code === '200') {
          this.$message.success("删除成功")
          this.loadComment(this.currentArticleId)
        } else {
          this.$message.error("删除失败")
        }
      })
    },
    loadComment(id) {
      this.request.get("/comment/article/2/" + id).then(res => {
        this.comments = res.data
      })
    },
    handleComment(id) {
      this.comment.articleId = id
      this.currentArticleId = id // 同时设置当前文章ID
      this.commentVis = true
    },
    view(id, content) {
      // 设置当前文章ID
      this.currentArticleId = id
      
      // 获取帖子详情信息
      const post = this.tableData.find(item => item.id === id)
      if (post) {
        this.currentPostAuthor = post.user || '宠物爱好者'
        this.currentPostTime = post.time
        this.currentPostViews = post.views || Math.floor(Math.random() * 500) + 50
      }
      
      this.content = content
      this.vis = true
      this.showQuickComment = false
      this.quickCommentContent = ''
      this.loadComment(id)
    },
    
    // 快速评论相关方法
    handleQuickComment() {
      if (!this.user.id) {
        this.$message.warning('请先登录后再评论')
        return
      }
      this.showQuickComment = !this.showQuickComment
      if (this.showQuickComment) {
        this.$nextTick(() => {
          const textarea = this.$el.querySelector('.comment-textarea .el-textarea__inner')
          if (textarea) {
            textarea.focus()
          }
        })
      }
    },
    
    submitQuickComment() {
      if (!this.quickCommentContent.trim()) {
        this.$message.warning('请输入评论内容')
        return
      }
      
      // 提交评论
      const newComment = {
        content: this.quickCommentContent,
        articleId: this.currentArticleId, // 使用当前文章ID
        type: 2
      }
      
      this.request.post("/comment", newComment).then(res => {
        if (res.code === '200') {
          this.$message({
            message: '评论发表成功！',
            type: 'success',
            duration: 2000
          })
          this.showQuickComment = false
          this.quickCommentContent = ''
          this.loadComment(this.currentArticleId)
        } else {
          this.$message.error(res.msg || '评论发表失败')
        }
      }).catch(() => {
        this.$message.error('网络错误，请稍后再试')
      })
    },
    // 绑定@imgAdd event
    imgAdd(pos, $file) {
      let $vm = this.$refs.md
      // 第一步.将图片上传到服务器.
      const formData = new FormData();
      formData.append('file', $file);
      axios({
        url: this.$store.state.baseApi+'/file/upload',
        method: 'post',
        data: formData,
        headers: {'Content-Type': 'multipart/form-data'},
      }).then((res) => {
        // 第二步.将返回的url替换到文本原位置![...](./0) -> ![...](url)
        $vm.$img2Url(pos, this.$store.state.baseApi + res.data);
      })
    },
    load() {
      this.request.get("/article/page", {
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
      if (!this.form.name) {
        this.$message.error("标题不能为空！")
        return
      }
      if (!this.form.content) {
        this.$message.error("内容不能为空！")
        return
      }
      this.request.post("/article", this.form).then(res => {
        if (res.code === '200') {
          this.$message.success("保存成功")
          this.dialogFormVisible = false
          this.load()
        } else {
          this.$message.error("保存失败")
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
      this.request.delete("/article/" + id).then(res => {
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
      this.request.post("/article/del/batch", ids).then(res => {
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
      window.open(this.$store.state.baseApi+"/article/export")
    },
    handleExcelImportSuccess() {
      this.$message.success("导入成功")
      this.load()
    }
  }
}
</script>


<style scoped>
/* 全局样式 */
.forum-container {
  min-height: calc(100vh - 60px);
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 0;
  margin: 0;
}

/* 页面头部 */
.forum-header {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.2);
  padding: 30px 0;
  margin-bottom: 30px;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.header-icon {
  width: 60px;
  height: 60px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 28px;
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.3);
}

.header-text h1 {
  margin: 0 0 5px 0;
  font-size: 28px;
  font-weight: 700;
  background: linear-gradient(135deg, #667eea, #764ba2);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.header-text p {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.header-stats {
  display: flex;
  gap: 30px;
}

.stat-item {
  text-align: center;
}

.stat-number {
  font-size: 24px;
  font-weight: 700;
  color: #667eea;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 12px;
  color: #666;
  margin: 0;
}

/* 搜索区域 */
.search-section {
  padding: 0 20px 30px;
}

.search-container {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 20px;
}

.search-box {
  display: flex;
  gap: 15px;
  flex: 1;
  max-width: 600px;
}

.search-input {
  flex: 1;
}

.search-input .el-input__inner {
  border-radius: 25px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  background: rgba(255, 255, 255, 0.9);
  padding-left: 20px;
  transition: all 0.3s ease;
}

.search-input .el-input__inner:focus {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.search-btn, .post-btn {
  border-radius: 25px;
  padding: 12px 30px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  transition: all 0.3s ease;
  border: none;
}

.search-btn {
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
}

.search-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.3);
}

.post-btn {
  background: linear-gradient(135deg, #56ab2f, #a8e6cf);
  color: white;
}

.post-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(86, 171, 47, 0.3);
}

/* 帖子区域 */
.posts-section {
  padding: 0 20px 30px;
}

.posts-container {
  max-width: 1200px;
  margin: 0 auto;
  display: grid;
  gap: 20px;
}

.post-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  padding: 25px;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  cursor: pointer;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.post-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 15px 40px rgba(0, 0, 0, 0.15);
  border-color: rgba(102, 126, 234, 0.3);
}

.post-header {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
  gap: 15px;
}

.post-info {
  flex: 1;
}

.post-author {
  font-weight: 600;
  color: #333;
  margin-bottom: 5px;
}

.post-time {
  font-size: 12px;
  color: #666;
  display: flex;
  align-items: center;
  gap: 5px;
}

.post-actions {
  opacity: 0;
  transition: opacity 0.3s ease;
}

.post-card:hover .post-actions {
  opacity: 1;
}

.post-content {
  margin-bottom: 20px;
}

.post-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin: 0 0 10px 0;
  line-height: 1.4;
}

.post-preview {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
  margin: 0;
}

.post-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 15px;
  border-top: 1px solid #f0f0f0;
}

.post-tags {
  display: flex;
  gap: 8px;
}

.post-stats {
  display: flex;
  align-items: center;
  gap: 15px;
}

.post-stats .stat-item {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 12px;
  color: #666;
}

.comment-btn {
  color: #667eea !important;
  font-weight: 500;
}

.comment-btn:hover {
  color: #764ba2 !important;
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 60px 20px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.empty-icon {
  font-size: 60px;
  color: #ddd;
  margin-bottom: 20px;
}

.empty-text h3 {
  margin: 0 0 10px 0;
  color: #666;
  font-weight: 500;
}

.empty-text p {
  margin: 0 0 30px 0;
  color: #999;
  font-size: 14px;
}

/* 分页区域 */
.pagination-section {
  padding: 20px;
}

.pagination-container {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  justify-content: center;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  padding: 20px;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

/* 对话框样式 */
.post-dialog .el-dialog {
  border-radius: 16px;
}

.post-form .el-form-item {
  margin-bottom: 25px;
}

.post-form .el-input__inner {
  border-radius: 8px;
  border: 2px solid #f0f0f0;
  transition: all 0.3s ease;
}

.post-form .el-input__inner:focus {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

/* 详情对话框样式 */
.detail-dialog {
  --primary-color: #667eea;
  --secondary-color: #764ba2;
  --accent-color: #f093fb;
  --success-color: #4facfe;
  --warning-color: #f093fb;
  --text-primary: #2c3e50;
  --text-secondary: #64748b;
  --bg-primary: #ffffff;
  --bg-secondary: #f8fafc;
  --border-color: #e2e8f0;
  --shadow-light: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
  --shadow-medium: 0 10px 15px -3px rgba(0, 0, 0, 0.1);
  --shadow-heavy: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
  --radius-sm: 8px;
  --radius-md: 12px;
  --radius-lg: 16px;
  --radius-xl: 20px;
}

.detail-dialog .el-dialog {
  border-radius: var(--radius-xl) !important;
  box-shadow: var(--shadow-heavy) !important;
  overflow: hidden;
  background: linear-gradient(135deg, 
    rgba(255, 255, 255, 0.95) 0%, 
    rgba(248, 250, 252, 0.95) 100%
  );
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.detail-dialog .el-dialog__header {
  padding: 0 !important;
  background: none !important;
  border-bottom: none !important;
}

.detail-dialog .el-dialog__body {
  padding: 0 !important;
  max-height: 85vh;
  overflow-y: auto;
}

/* 对话框标题 */
.dialog-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 25px 30px;
  background: linear-gradient(135deg, var(--primary-color), var(--secondary-color));
  color: white;
  position: relative;
  overflow: hidden;
}

.dialog-title::before {
  content: '';
  position: absolute;
  top: -50%;
  right: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle, rgba(255,255,255,0.15) 0%, transparent 70%);
  animation: shimmer 3s ease-in-out infinite;
}

@keyframes shimmer {
  0%, 100% { transform: translateX(-100%) translateY(-100%); }
  50% { transform: translateX(0) translateY(0); }
}

.title-content {
  display: flex;
  align-items: center;
  gap: 15px;
  z-index: 1;
  position: relative;
}

.title-icon {
  width: 50px;
  height: 50px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.title-text h2 {
  margin: 0 0 5px 0;
  font-size: 24px;
  font-weight: 700;
  text-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.title-text p {
  margin: 0;
  font-size: 14px;
  opacity: 0.9;
  font-weight: 400;
}

.close-btn {
  background: rgba(255, 255, 255, 0.2) !important;
  border: 1px solid rgba(255, 255, 255, 0.3) !important;
  color: white !important;
  backdrop-filter: blur(10px);
  transition: all 0.3s ease !important;
  z-index: 1;
  position: relative;
}

.close-btn:hover {
  background: rgba(255, 255, 255, 0.3) !important;
  transform: scale(1.1) !important;
}

/* 帖子详情容器 */
.post-detail-container {
  display: grid;
  grid-template-columns: 1fr 400px;
  gap: 30px;
  padding: 30px;
  min-height: 70vh;
}

/* 帖子内容区域 */
.post-content-section {
  background: var(--bg-primary);
  border-radius: var(--radius-lg);
  overflow: hidden;
  box-shadow: var(--shadow-medium);
  border: 1px solid var(--border-color);
}

.content-header {
  padding: 25px 30px;
  border-bottom: 1px solid var(--border-color);
  background: linear-gradient(135deg, 
    rgba(102, 126, 234, 0.05) 0%, 
    rgba(118, 75, 162, 0.05) 100%
  );
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.author-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.author-avatar {
  box-shadow: var(--shadow-light);
  border: 3px solid white;
}

.author-details {
  flex: 1;
}

.author-name {
  font-size: 18px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 5px;
}

.post-meta {
  display: flex;
  gap: 20px;
  align-items: center;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 13px;
  color: var(--text-secondary);
}

.meta-item i {
  color: var(--primary-color);
}

.post-actions {
  display: flex;
  gap: 10px;
}

.action-btn {
  padding: 8px 16px !important;
  border-radius: var(--radius-md) !important;
  font-size: 13px !important;
  font-weight: 500 !important;
  transition: all 0.3s ease !important;
  border: 1px solid var(--border-color) !important;
  background: white !important;
}

.action-btn:hover {
  transform: translateY(-2px) !important;
  box-shadow: var(--shadow-light) !important;
}

.like-btn:hover {
  color: #e74c3c !important;
  border-color: #e74c3c !important;
  background: rgba(231, 76, 60, 0.05) !important;
}

.share-btn:hover {
  color: var(--primary-color) !important;
  border-color: var(--primary-color) !important;
  background: rgba(102, 126, 234, 0.05) !important;
}

.collect-btn:hover {
  color: #f39c12 !important;
  border-color: #f39c12 !important;
  background: rgba(243, 156, 18, 0.05) !important;
}

.content-body {
  padding: 30px;
  background: var(--bg-primary);
}

.md-viewer {
  border: none !important;
  border-radius: var(--radius-md);
  overflow: hidden;
  box-shadow: inset 0 1px 3px 0 rgba(0, 0, 0, 0.05);
  background: #fafbfc;
}

/* 评论区域样式 */
.comments-section {
  background: var(--bg-primary);
  border-radius: var(--radius-lg);
  border: 1px solid var(--border-color);
  box-shadow: var(--shadow-medium);
  overflow: hidden;
  height: fit-content;
}

.comments-header {
  padding: 25px 30px;
  background: linear-gradient(135deg, 
    rgba(240, 147, 251, 0.05) 0%, 
    rgba(79, 172, 254, 0.05) 100%
  );
  border-bottom: 1px solid var(--border-color);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 15px;
}

.comments-header h3 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: var(--text-primary);
  display: flex;
  align-items: center;
  gap: 8px;
}

.comments-header h3 i {
  color: var(--accent-color);
  font-size: 22px;
}

.comments-count {
  background: linear-gradient(135deg, var(--accent-color), var(--success-color));
  color: white;
  padding: 6px 14px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  box-shadow: var(--shadow-light);
}

.quick-comment-btn {
  background: linear-gradient(135deg, var(--accent-color), var(--success-color)) !important;
  border: none !important;
  border-radius: 20px !important;
  padding: 10px 20px !important;
  font-weight: 600 !important;
  box-shadow: var(--shadow-light) !important;
  transition: all 0.3s ease !important;
}

.quick-comment-btn:hover {
  transform: translateY(-2px) !important;
  box-shadow: var(--shadow-medium) !important;
}

/* 快速评论表单 */
.quick-comment-form {
  padding: 25px 30px;
  background: linear-gradient(135deg, 
    rgba(102, 126, 234, 0.02) 0%, 
    rgba(240, 147, 251, 0.02) 100%
  );
  border-bottom: 1px solid var(--border-color);
}

.comment-form-header {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 15px;
}

.user-avatar {
  box-shadow: var(--shadow-light);
  border: 2px solid white;
}

.form-title h4 {
  margin: 0 0 3px 0;
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
}

.form-title p {
  margin: 0;
  font-size: 13px;
  color: var(--text-secondary);
}

.comment-textarea {
  margin-bottom: 15px;
}

.comment-textarea .el-textarea__inner {
  border: 2px solid var(--border-color) !important;
  border-radius: var(--radius-md) !important;
  padding: 15px !important;
  font-size: 14px !important;
  line-height: 1.6 !important;
  transition: all 0.3s ease !important;
  background: white !important;
}

.comment-textarea .el-textarea__inner:focus {
  border-color: var(--primary-color) !important;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1) !important;
}

.comment-form-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.form-tips {
  display: flex;
  align-items: center;
  gap: 6px;
  color: var(--text-secondary);
  font-size: 12px;
}

.form-tips i {
  color: var(--primary-color);
}

.form-actions {
  display: flex;
  gap: 10px;
}

/* 评论列表 */
.comments-list {
  padding: 25px 30px;
  display: flex;
  flex-direction: column;
  gap: 20px;
  max-height: 60vh;
  overflow-y: auto;
}

.comment-item {
  display: flex;
  gap: 15px;
  padding: 20px;
  background: var(--bg-secondary);
  border-radius: var(--radius-md);
  border: 1px solid var(--border-color);
  transition: all 0.3s ease;
  position: relative;
}

.comment-item:hover {
  background: white;
  box-shadow: var(--shadow-light);
  transform: translateY(-2px);
}

.comment-item::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 3px;
  background: linear-gradient(135deg, var(--primary-color), var(--accent-color));
  border-radius: 0 2px 2px 0;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.comment-item:hover::before {
  opacity: 1;
}

.comment-avatar {
  flex-shrink: 0;
}

.comment-avatar .el-avatar {
  box-shadow: var(--shadow-light);
  border: 2px solid white;
}

.comment-content {
  flex: 1;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.comment-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.comment-author {
  font-weight: 600;
  color: var(--text-primary);
  font-size: 14px;
}

.comment-time {
  font-size: 12px;
  color: var(--text-secondary);
  display: flex;
  align-items: center;
  gap: 4px;
}

.comment-actions {
  display: flex;
  gap: 8px;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.comment-item:hover .comment-actions {
  opacity: 1;
}

.reply-btn {
  color: var(--primary-color) !important;
  font-size: 12px !important;
  padding: 4px 8px !important;
  border-radius: var(--radius-sm) !important;
  transition: all 0.3s ease !important;
}

.reply-btn:hover {
  background: rgba(102, 126, 234, 0.1) !important;
}

.delete-comment-btn {
  color: #e74c3c !important;
  font-size: 12px !important;
  padding: 4px 8px !important;
  border-radius: var(--radius-sm) !important;
  transition: all 0.3s ease !important;
}

.delete-comment-btn:hover {
  background: rgba(231, 76, 60, 0.1) !important;
}

.comment-text {
  font-size: 14px;
  line-height: 1.6;
  color: var(--text-primary);
  margin-bottom: 10px;
}

.comment-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.comment-stats {
  display: flex;
  gap: 15px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 12px;
  color: var(--text-secondary);
  cursor: pointer;
  transition: color 0.3s ease;
}

.stat-item:hover {
  color: var(--primary-color);
}

.stat-item i {
  font-size: 14px;
}

/* 空状态 */
.no-comments {
  text-align: center;
  padding: 60px 20px;
  color: var(--text-secondary);
}

.empty-icon {
  font-size: 64px;
  color: var(--border-color);
  margin-bottom: 20px;
}

.no-comments h4 {
  font-size: 18px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 8px 0;
}

.no-comments p {
  font-size: 14px;
  color: var(--text-secondary);
  margin: 0 0 25px 0;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .post-detail-container {
    grid-template-columns: 1fr;
    gap: 20px;
  }
  
  .comments-section {
    order: 2;
  }
}

@media (max-width: 768px) {
  .detail-dialog .el-dialog {
    width: 95% !important;
    margin: 1vh auto !important;
  }
  
  .post-detail-container {
    padding: 20px;
    gap: 15px;
  }
  
  .dialog-title {
    padding: 20px;
  }
  
  .title-text h2 {
    font-size: 20px;
  }
  
  .content-header {
    flex-direction: column;
    gap: 15px;
    align-items: flex-start;
  }
  
  .post-actions {
    width: 100%;
    justify-content: flex-end;
  }
  
  .comments-header {
    flex-direction: column;
    gap: 15px;
    align-items: flex-start;
  }
  
  .comment-form-footer {
    flex-direction: column;
    gap: 15px;
    align-items: flex-start;
  }
  
  .comment-header {
    flex-direction: column;
    gap: 10px;
    align-items: flex-start;
  }
  
  .comment-actions {
    opacity: 1;
  }
}

/* 动画效果 */
@keyframes slideInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.comment-item {
  animation: slideInUp 0.4s ease forwards;
}

.quick-comment-form {
  animation: slideInUp 0.3s ease forwards;
}

.post-detail-container {
  animation: fadeIn 0.5s ease forwards;
}

/* 评论对话框 */
.comment-dialog .el-dialog {
  border-radius: 16px;
}

.comment-form .el-textarea__inner {
  border-radius: 8px;
  border: 2px solid #f0f0f0;
  transition: all 0.3s ease;
  resize: vertical;
}

.comment-form .el-textarea__inner:focus {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .header-content {
    flex-direction: column;
    gap: 20px;
    text-align: center;
  }
  
  .header-stats {
    justify-content: center;
  }
  
  .search-container {
    flex-direction: column;
    gap: 15px;
  }
  
  .search-box {
    max-width: none;
  }
  
  .action-buttons {
    width: 100%;
    display: flex;
    justify-content: center;
  }
  
  .post-card {
    padding: 20px;
  }
  
  .post-footer {
    flex-direction: column;
    gap: 15px;
    align-items: flex-start;
  }
  
  .post-stats {
    width: 100%;
    justify-content: space-between;
  }
  
  .comment-item {
    flex-direction: column;
    gap: 10px;
  }
  
  .comment-header {
    flex-direction: column;
    gap: 10px;
    align-items: flex-start;
  }
}

@media (max-width: 480px) {
  .forum-container {
    padding: 0;
  }
  
  .search-section,
  .posts-section,
  .pagination-section {
    padding: 0 10px 20px;
  }
  
  .header-content {
    padding: 0 15px;
  }
  
  .post-card {
    padding: 15px;
    border-radius: 12px;
  }
  
  .post-title {
    font-size: 16px;
  }
}
</style>

<style>
.headerBg {
  background: #eee!important;
}

/* mavon-editor 全局样式优化 */
.v-note-wrapper {
  border-radius: 8px !important;
  border: 2px solid #f0f0f0 !important;
  overflow: hidden;
}

.v-note-wrapper.fullscreen {
  border-radius: 0 !important;
}

/* 对话框样式优化 */
.el-dialog {
  border-radius: 16px !important;
}

.el-dialog__header {
  background: linear-gradient(135deg, #667eea, #764ba2) !important;
  color: white !important;
  border-radius: 16px 16px 0 0 !important;
  padding: 20px !important;
}

.el-dialog__title {
  color: white !important;
  font-weight: 600 !important;
}

.el-dialog__headerbtn .el-dialog__close {
  color: white !important;
  font-size: 20px !important;
}

.el-dialog__body {
  padding: 30px !important;
}

.el-dialog__footer {
  padding: 20px 30px !important;
  background: #f8f9fa !important;
  border-radius: 0 0 16px 16px !important;
}

/* 按钮样式优化 */
.el-button {
  border-radius: 8px !important;
  font-weight: 500 !important;
  transition: all 0.3s ease !important;
}

.el-button--primary {
  background: linear-gradient(135deg, #667eea, #764ba2) !important;
  border: none !important;
}

.el-button--primary:hover {
  background: linear-gradient(135deg, #5a6fd8, #6a4190) !important;
  transform: translateY(-1px) !important;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3) !important;
}

.el-button--success {
  background: linear-gradient(135deg, #56ab2f, #a8e6cf) !important;
  border: none !important;
}

.el-button--success:hover {
  background: linear-gradient(135deg, #4a9b28, #96deb7) !important;
  transform: translateY(-1px) !important;
  box-shadow: 0 4px 15px rgba(86, 171, 47, 0.3) !important;
}

/* 分页按钮样式 */
.el-pagination.is-background .el-pager li:not(.disabled).active {
  background-color: #667eea !important;
  color: white !important;
}

.el-pagination.is-background .el-pager li:not(.disabled):hover {
  color: #667eea !important;
}

/* 标签样式 */
.el-tag {
  border-radius: 20px !important;
  border: none !important;
  font-size: 11px !important;
  padding: 4px 12px !important;
}

.el-tag--info {
  background: linear-gradient(135deg, #667eea, #764ba2) !important;
  color: white !important;
}
</style>
