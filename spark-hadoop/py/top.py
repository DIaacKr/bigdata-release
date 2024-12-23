#获取系统信息
import psutil
import time
import csv
import sys

def main(interval=5, loops=1200):
    # 获取初始磁盘 I/O 计数
    last_disk_io = psutil.disk_io_counters(perdisk=False)

    with open('system_usage.csv', 'w', newline='') as file:
        writer = csv.writer(file)
        writer.writerow(['Time', 'CPU%', 'Mem%', 'Disk Read (MB)', 'Disk Write (MB)'])

        for _ in range(loops):
            try:
                cpu_percent = psutil.cpu_percent(interval=interval)
                mem_info = psutil.virtual_memory()
                current_disk_io = psutil.disk_io_counters(perdisk=False)

                # 计算增量
                disk_read_mb = (current_disk_io.read_bytes - last_disk_io.read_bytes) / 1024 / 1024
                disk_write_mb = (current_disk_io.write_bytes - last_disk_io.write_bytes) / 1024 / 1024

                writer.writerow([
                    time.strftime("%Y-%m-%d %H:%M:%S"),
                    cpu_percent,
                    mem_info.percent,
                    disk_read_mb,
                    disk_write_mb
                ])

                # 更新最后的磁盘 I/O 计数
                last_disk_io = current_disk_io

                # 等待剩余的时间以达到指定的采样间隔
                time.sleep(max(0, interval - (time.time() % interval)))

            except KeyboardInterrupt:
                print("Monitoring interrupted by user.")
                break
            except Exception as e:
                print(f"An error occurred: {e}")
                break

if __name__ == "__main__":
    if len(sys.argv) > 1:
        try:
            interval = int(sys.argv[1])
            loops = int(sys.argv[2]) if len(sys.argv) > 2 else 60
            main(interval, loops)
        except ValueError:
            print("Please provide valid integer values for interval and loops.")
    else:
        main()
