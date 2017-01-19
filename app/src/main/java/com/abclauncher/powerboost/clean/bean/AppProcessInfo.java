/*
 * Copyright (C) 2012 www.amsoft.cn
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.abclauncher.powerboost.clean.bean;

import android.graphics.drawable.Drawable;

import java.io.Serializable;


// TODO: Auto-generated Javadoc

/**
 * @author
 * @version v1.0
 */
public class AppProcessInfo implements Comparable<AppProcessInfo> ,Serializable{

    /**
     * The app name.
     */
    public String appName;

    /**
     * The name of the process that this object is associated with.
     */
    public String processName;

    /**
     * The pid of this process; 0 if none.
     */
    public int pid;

    /**
     * The user id of this process.
     */
    public String uid;

    /**
     * The icon.
     */
    public Drawable icon;

    /**
     * 占用的内存.
     */
    public long memory;

    /**
     * 占用的内存.
     */
    public int cpu;

    /**
     * 进程的状态，其中S表示休眠，R表示正在运行，Z表示僵死状态，N表示该进程优先值是负数.
     */
    public String status;

    /**
     * 当前使用的线程数.
     */
    public String threadsCount;


    public boolean checked=true;

    /**
     * 是否是系统进程.
     */
    public boolean isSystem;

    /**
     * 级别
     */
    public int level;
    /**
     * Instantiates a new ab process info.
     */
    public AppProcessInfo() {
        super();
    }

    /**
     * Instantiates a new ab process info.
     *
     * @param processName the process name
     * @param pid         the pid
     * @param uid         the uid
     */
    public AppProcessInfo(String processName, int pid, String uid) {
        super();
        this.processName = processName;
        this.pid = pid;
        this.uid = uid;
    }


    @Override
    public String toString() {
        return "processName: " + processName + ", appName: " + appName
                + ", memory : " + memory *1f/ (1000*1024) +
                "M" + ",cpu=" + cpu;
    }

    /* (non-Javadoc)
         * @see java.lang.Comparable#compareTo(java.lang.Object)
         */
    @Override
    public int compareTo(AppProcessInfo another) {
        if (this.processName.compareTo(another.processName) == 0) {
            if (this.cpu < another.cpu) {
                return 1;
            } else if (this.cpu == another.cpu) {
                return 0;
            } else {
                return -1;
            }
        } else {
            return this.processName.compareTo(another.processName);
        }
    }

}
