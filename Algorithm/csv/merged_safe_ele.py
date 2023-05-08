import pandas as pd

police_df = pd.read_csv('csv/경찰서.csv')
bell_df = pd.read_csv('csv/안전비상벨.csv')
house_df = pd.read_csv('csv/여성안전지킴이집.csv')

# type 열 추가
police_df['type'] = '경찰서'
bell_df['type'] = '안전비상벨'
house_df['type'] = '여성안전지킴이집'

# 데이터프레임 병합
merged_df = pd.concat([police_df, bell_df, house_df], axis=0, ignore_index=True)
merged_df.reset_index(drop=True, inplace=True)

# csv 파일로 저장

# remove duplicates
merged_df = merged_df.drop_duplicates()
merged_df.to_csv('merged_safe.csv', index=False)