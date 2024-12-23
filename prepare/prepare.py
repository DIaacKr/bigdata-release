import pandas as pd
from sklearn.preprocessing import StandardScaler

# 定义列名
column_names = [
    'status_code', 'protocol_type', 'service_type', 'connection_status',
    'duration', 'source_bytes', 'destination_bytes', 'count', 'same_srv_rate',
    'srv_count', 'serror_rate', 'srv_serror_rate', 'rerror_rate',
    'srv_rerror_rate', 'flag', 'land', 'wrong_fragment', 'urgent',
    'hot', 'num_failed_logins', 'logged_in', 'num_compromised',
    'root_shell', 'su_attempted', 'num_root', 'num_file_creations',
    'num_shells', 'num_access_files', 'num_outbound_cmds',
    'is_host_login', 'is_guest_login', 'count_2', 'srv_count_2',
    'serror_rate_2', 'srv_serror_rate_2', 'rerror_rate_2',
    'srv_rerror_rate_2', 'label'
]

# 读取CSV文件并指定列名
data_path = 'data.csv'
df = pd.read_csv(data_path, names=column_names)

# 删除不需要的列
columns_to_drop = [
    'status_code', 'protocol_type', 'service_type', 'connection_status', 
    'flag', 'land', 'wrong_fragment', 'urgent', 'hot', 'num_failed_logins', 
    'logged_in', 'num_compromised', 'root_shell', 'su_attempted', 'num_root', 
    'num_file_creations', 'num_shells', 'num_access_files', 'num_outbound_cmds', 
    'is_host_login', 'is_guest_login', 'label'
]
df_cleaned = df.drop(columns=columns_to_drop, axis=1)

# 标准化数值型特征
numeric_features = df_cleaned.select_dtypes(include=['int64', 'float64']).columns.tolist()
scaler = StandardScaler()
df_cleaned[numeric_features] = scaler.fit_transform(df_cleaned[numeric_features])

# 保存处理后的数据到新的CSV文件
output_path = 'data_10_processed.csv'
df_cleaned.to_csv(output_path, index=False)

print(f"Processed data saved to {output_path}")
