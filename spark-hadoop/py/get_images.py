#画图，需要在同级目录下创建result文件夹,以top.py的输出文件作为输入
import pandas as pd
import matplotlib.pyplot as plt
import os

# 确保 'result' 文件夹存在
output_dir = 'result'
if not os.path.exists(output_dir):
    os.makedirs(output_dir)

# 读取 CSV 文件到 DataFrame
df = pd.read_csv('system_usage.csv')

# 将 'Time' 列转换为 datetime 格式
df['Time'] = pd.to_datetime(df['Time'])

# 设置 'Time' 为索引
df.set_index('Time', inplace=True)

# 计算相对于起始时间的分钟数
df['Minutes'] = (df.index - df.index[0]).total_seconds() / 60

# 定义绘图函数
def plot_and_save(x_data, y_data, title, ylabel, filename, color='tab:blue', linestyle='-'):
    fig, ax = plt.subplots(figsize=(14, 7))
    
    # 绘制数据
    ax.plot(x_data, y_data, color=color, linestyle=linestyle)
    
    # 设置标题和标签
    ax.set_title(title)
    ax.set_xlabel('Time (Minutes)')
    ax.set_ylabel(ylabel)
    
    # 设置网格
    ax.grid(True)
    
    # 自动旋转日期标记以防止重叠
    plt.tight_layout()
    
    # 保存图表
    save_path = os.path.join(output_dir, filename)
    fig.savefig(save_path, bbox_inches='tight')
    plt.close(fig)  # 关闭当前图形以释放内存
    
    return save_path

# 绘制 CPU% 图表并保存
cpu_usage_path = plot_and_save(
    df['Minutes'], 
    df['CPU%'], 
    'CPU Usage Over Time', 
    'CPU Usage (%)', 
    'cpu_usage.png'
)

# 绘制 Mem% 图表并保存
memory_usage_path = plot_and_save(
    df['Minutes'], 
    df['Mem%'], 
    'Memory Usage Over Time', 
    'Memory Usage (%)', 
    'memory_usage.png',
    color='tab:orange'
)

# 绘制 Disk Read (MB) 和 Disk Write (MB) 图表并保存
fig, ax = plt.subplots(figsize=(14, 7))

# 绘制数据
ax.plot(df['Minutes'], df['Disk Read (MB)'], label='Disk Read (MB)', color='tab:green', linestyle='--')
ax.plot(df['Minutes'], df['Disk Write (MB)'], label='Disk Write (MB)', color='tab:red', linestyle=':')

# 设置标题和标签
ax.set_title('Disk I/O Over Time')
ax.set_xlabel('Time (Minutes)')
ax.set_ylabel('Disk I/O (MB)')

# 设置网格和图例
ax.grid(True)
ax.legend(loc='upper left')

# 自动调整布局
plt.tight_layout()

# 保存磁盘I/O图表
disk_io_path = os.path.join(output_dir, 'disk_io.png')
fig.savefig(disk_io_path, bbox_inches='tight')
plt.close(fig)  # 关闭当前图形以释放内存

print(f"Charts have been saved to {output_dir} folder.")
