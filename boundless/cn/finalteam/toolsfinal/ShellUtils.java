package cn.finalteam.toolsfinal;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class ShellUtils {
    public static final String COMMAND_EXIT = "exit\n";
    public static final String COMMAND_LINE_END = "\n";
    public static final String COMMAND_SH = "sh";
    public static final String COMMAND_SU = "su";

    public static class CommandResult {
        public String errorMsg;
        public int result;
        public String successMsg;

        public CommandResult(int result) {
            this.result = result;
        }

        public CommandResult(int result, String successMsg, String errorMsg) {
            this.result = result;
            this.successMsg = successMsg;
            this.errorMsg = errorMsg;
        }
    }

    private ShellUtils() {
        throw new AssertionError();
    }

    public static boolean checkRootPermission() {
        return execCommand("echo root", true, false).result == 0;
    }

    public static CommandResult execCommand(String command, boolean isRoot) {
        return execCommand(new String[]{command}, isRoot, true);
    }

    public static CommandResult execCommand(List<String> commands, boolean isRoot) {
        String[] strArr;
        if (commands == null) {
            strArr = null;
        } else {
            strArr = (String[]) commands.toArray(new String[0]);
        }
        return execCommand(strArr, isRoot, true);
    }

    public static CommandResult execCommand(String[] commands, boolean isRoot) {
        return execCommand(commands, isRoot, true);
    }

    public static CommandResult execCommand(String command, boolean isRoot, boolean isNeedResultMsg) {
        return execCommand(new String[]{command}, isRoot, isNeedResultMsg);
    }

    public static CommandResult execCommand(List<String> commands, boolean isRoot, boolean isNeedResultMsg) {
        String[] strArr;
        if (commands == null) {
            strArr = null;
        } else {
            strArr = (String[]) commands.toArray(new String[0]);
        }
        return execCommand(strArr, isRoot, isNeedResultMsg);
    }

    public static CommandResult execCommand(String[] commands, boolean isRoot, boolean isNeedResultMsg) {
        StringBuilder errorMsg;
        IOException e;
        String str;
        String str2;
        Throwable th;
        Exception e2;
        BufferedReader successResult;
        int result = -1;
        if (commands == null || commands.length == 0) {
            return new CommandResult(-1, null, null);
        }
        Process process = null;
        BufferedReader successResult2 = null;
        BufferedReader errorResult = null;
        StringBuilder stringBuilder = null;
        StringBuilder stringBuilder2 = null;
        DataOutputStream dataOutputStream = null;
        try {
            process = Runtime.getRuntime().exec(isRoot ? COMMAND_SU : COMMAND_SH);
            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            try {
                for (String command : commands) {
                    if (command != null) {
                        os.write(command.getBytes());
                        os.writeBytes("\n");
                        os.flush();
                    }
                }
                os.writeBytes(COMMAND_EXIT);
                os.flush();
                result = process.waitFor();
                if (isNeedResultMsg) {
                    StringBuilder successMsg = new StringBuilder();
                    try {
                        errorMsg = new StringBuilder();
                    } catch (IOException e3) {
                        e = e3;
                        dataOutputStream = os;
                        stringBuilder = successMsg;
                        try {
                            e.printStackTrace();
                            if (dataOutputStream != null) {
                                try {
                                    dataOutputStream.close();
                                } catch (IOException e4) {
                                    e4.printStackTrace();
                                    if (process != null) {
                                        process.destroy();
                                    }
                                    if (stringBuilder == null) {
                                        str = null;
                                    } else {
                                        str = stringBuilder.toString();
                                    }
                                    if (stringBuilder2 == null) {
                                        str2 = null;
                                    } else {
                                        str2 = stringBuilder2.toString();
                                    }
                                    return new CommandResult(result, str, str2);
                                }
                            }
                            if (successResult2 != null) {
                                successResult2.close();
                            }
                            if (errorResult != null) {
                                errorResult.close();
                            }
                            if (process != null) {
                                process.destroy();
                            }
                            if (stringBuilder == null) {
                                str = stringBuilder.toString();
                            } else {
                                str = null;
                            }
                            if (stringBuilder2 == null) {
                                str2 = stringBuilder2.toString();
                            } else {
                                str2 = null;
                            }
                            return new CommandResult(result, str, str2);
                        } catch (Throwable th2) {
                            th = th2;
                            if (dataOutputStream != null) {
                                try {
                                    dataOutputStream.close();
                                } catch (IOException e42) {
                                    e42.printStackTrace();
                                    if (process != null) {
                                        process.destroy();
                                    }
                                    throw th;
                                }
                            }
                            if (successResult2 != null) {
                                successResult2.close();
                            }
                            if (errorResult != null) {
                                errorResult.close();
                            }
                            if (process != null) {
                                process.destroy();
                            }
                            throw th;
                        }
                    } catch (Exception e5) {
                        e2 = e5;
                        dataOutputStream = os;
                        stringBuilder = successMsg;
                        e2.printStackTrace();
                        if (dataOutputStream != null) {
                            try {
                                dataOutputStream.close();
                            } catch (IOException e422) {
                                e422.printStackTrace();
                                if (process != null) {
                                    process.destroy();
                                }
                                if (stringBuilder == null) {
                                    str = stringBuilder.toString();
                                } else {
                                    str = null;
                                }
                                if (stringBuilder2 == null) {
                                    str2 = stringBuilder2.toString();
                                } else {
                                    str2 = null;
                                }
                                return new CommandResult(result, str, str2);
                            }
                        }
                        if (successResult2 != null) {
                            successResult2.close();
                        }
                        if (errorResult != null) {
                            errorResult.close();
                        }
                        if (process != null) {
                            process.destroy();
                        }
                        if (stringBuilder == null) {
                            str = null;
                        } else {
                            str = stringBuilder.toString();
                        }
                        if (stringBuilder2 == null) {
                            str2 = null;
                        } else {
                            str2 = stringBuilder2.toString();
                        }
                        return new CommandResult(result, str, str2);
                    } catch (Throwable th3) {
                        th = th3;
                        dataOutputStream = os;
                        stringBuilder = successMsg;
                        if (dataOutputStream != null) {
                            dataOutputStream.close();
                        }
                        if (successResult2 != null) {
                            successResult2.close();
                        }
                        if (errorResult != null) {
                            errorResult.close();
                        }
                        if (process != null) {
                            process.destroy();
                        }
                        throw th;
                    }
                    try {
                        successResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    } catch (IOException e6) {
                        e422 = e6;
                        dataOutputStream = os;
                        stringBuilder2 = errorMsg;
                        stringBuilder = successMsg;
                        e422.printStackTrace();
                        if (dataOutputStream != null) {
                            dataOutputStream.close();
                        }
                        if (successResult2 != null) {
                            successResult2.close();
                        }
                        if (errorResult != null) {
                            errorResult.close();
                        }
                        if (process != null) {
                            process.destroy();
                        }
                        if (stringBuilder == null) {
                            str = stringBuilder.toString();
                        } else {
                            str = null;
                        }
                        if (stringBuilder2 == null) {
                            str2 = stringBuilder2.toString();
                        } else {
                            str2 = null;
                        }
                        return new CommandResult(result, str, str2);
                    } catch (Exception e7) {
                        e2 = e7;
                        dataOutputStream = os;
                        stringBuilder2 = errorMsg;
                        stringBuilder = successMsg;
                        e2.printStackTrace();
                        if (dataOutputStream != null) {
                            dataOutputStream.close();
                        }
                        if (successResult2 != null) {
                            successResult2.close();
                        }
                        if (errorResult != null) {
                            errorResult.close();
                        }
                        if (process != null) {
                            process.destroy();
                        }
                        if (stringBuilder == null) {
                            str = null;
                        } else {
                            str = stringBuilder.toString();
                        }
                        if (stringBuilder2 == null) {
                            str2 = null;
                        } else {
                            str2 = stringBuilder2.toString();
                        }
                        return new CommandResult(result, str, str2);
                    } catch (Throwable th4) {
                        th = th4;
                        dataOutputStream = os;
                        stringBuilder2 = errorMsg;
                        stringBuilder = successMsg;
                        if (dataOutputStream != null) {
                            dataOutputStream.close();
                        }
                        if (successResult2 != null) {
                            successResult2.close();
                        }
                        if (errorResult != null) {
                            errorResult.close();
                        }
                        if (process != null) {
                            process.destroy();
                        }
                        throw th;
                    }
                    try {
                        String s;
                        BufferedReader errorResult2 = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                        while (true) {
                            try {
                                s = successResult.readLine();
                                if (s == null) {
                                    break;
                                }
                                successMsg.append(s);
                            } catch (IOException e8) {
                                e422 = e8;
                                dataOutputStream = os;
                                stringBuilder2 = errorMsg;
                                stringBuilder = successMsg;
                                errorResult = errorResult2;
                                successResult2 = successResult;
                            } catch (Exception e9) {
                                e2 = e9;
                                dataOutputStream = os;
                                stringBuilder2 = errorMsg;
                                stringBuilder = successMsg;
                                errorResult = errorResult2;
                                successResult2 = successResult;
                            } catch (Throwable th5) {
                                th = th5;
                                dataOutputStream = os;
                                stringBuilder2 = errorMsg;
                                stringBuilder = successMsg;
                                errorResult = errorResult2;
                                successResult2 = successResult;
                            }
                        }
                        while (true) {
                            s = errorResult2.readLine();
                            if (s == null) {
                                break;
                            }
                            errorMsg.append(s);
                        }
                        stringBuilder2 = errorMsg;
                        stringBuilder = successMsg;
                        errorResult = errorResult2;
                        successResult2 = successResult;
                    } catch (IOException e10) {
                        e422 = e10;
                        dataOutputStream = os;
                        stringBuilder2 = errorMsg;
                        stringBuilder = successMsg;
                        successResult2 = successResult;
                        e422.printStackTrace();
                        if (dataOutputStream != null) {
                            dataOutputStream.close();
                        }
                        if (successResult2 != null) {
                            successResult2.close();
                        }
                        if (errorResult != null) {
                            errorResult.close();
                        }
                        if (process != null) {
                            process.destroy();
                        }
                        if (stringBuilder == null) {
                            str = null;
                        } else {
                            str = stringBuilder.toString();
                        }
                        if (stringBuilder2 == null) {
                            str2 = null;
                        } else {
                            str2 = stringBuilder2.toString();
                        }
                        return new CommandResult(result, str, str2);
                    } catch (Exception e11) {
                        e2 = e11;
                        dataOutputStream = os;
                        stringBuilder2 = errorMsg;
                        stringBuilder = successMsg;
                        successResult2 = successResult;
                        e2.printStackTrace();
                        if (dataOutputStream != null) {
                            dataOutputStream.close();
                        }
                        if (successResult2 != null) {
                            successResult2.close();
                        }
                        if (errorResult != null) {
                            errorResult.close();
                        }
                        if (process != null) {
                            process.destroy();
                        }
                        if (stringBuilder == null) {
                            str = stringBuilder.toString();
                        } else {
                            str = null;
                        }
                        if (stringBuilder2 == null) {
                            str2 = stringBuilder2.toString();
                        } else {
                            str2 = null;
                        }
                        return new CommandResult(result, str, str2);
                    } catch (Throwable th6) {
                        th = th6;
                        dataOutputStream = os;
                        stringBuilder2 = errorMsg;
                        stringBuilder = successMsg;
                        successResult2 = successResult;
                        if (dataOutputStream != null) {
                            dataOutputStream.close();
                        }
                        if (successResult2 != null) {
                            successResult2.close();
                        }
                        if (errorResult != null) {
                            errorResult.close();
                        }
                        if (process != null) {
                            process.destroy();
                        }
                        throw th;
                    }
                }
                if (os != null) {
                    try {
                        os.close();
                    } catch (IOException e4222) {
                        e4222.printStackTrace();
                    }
                }
                if (successResult2 != null) {
                    successResult2.close();
                }
                if (errorResult != null) {
                    errorResult.close();
                }
                if (process != null) {
                    process.destroy();
                    dataOutputStream = os;
                }
            } catch (IOException e12) {
                e4222 = e12;
                dataOutputStream = os;
            } catch (Exception e13) {
                e2 = e13;
                dataOutputStream = os;
            } catch (Throwable th7) {
                th = th7;
                dataOutputStream = os;
            }
        } catch (IOException e14) {
            e4222 = e14;
            e4222.printStackTrace();
            if (dataOutputStream != null) {
                dataOutputStream.close();
            }
            if (successResult2 != null) {
                successResult2.close();
            }
            if (errorResult != null) {
                errorResult.close();
            }
            if (process != null) {
                process.destroy();
            }
            if (stringBuilder == null) {
                str = stringBuilder.toString();
            } else {
                str = null;
            }
            if (stringBuilder2 == null) {
                str2 = stringBuilder2.toString();
            } else {
                str2 = null;
            }
            return new CommandResult(result, str, str2);
        } catch (Exception e15) {
            e2 = e15;
            e2.printStackTrace();
            if (dataOutputStream != null) {
                dataOutputStream.close();
            }
            if (successResult2 != null) {
                successResult2.close();
            }
            if (errorResult != null) {
                errorResult.close();
            }
            if (process != null) {
                process.destroy();
            }
            if (stringBuilder == null) {
                str = null;
            } else {
                str = stringBuilder.toString();
            }
            if (stringBuilder2 == null) {
                str2 = null;
            } else {
                str2 = stringBuilder2.toString();
            }
            return new CommandResult(result, str, str2);
        }
        if (stringBuilder == null) {
            str = null;
        } else {
            str = stringBuilder.toString();
        }
        if (stringBuilder2 == null) {
            str2 = null;
        } else {
            str2 = stringBuilder2.toString();
        }
        return new CommandResult(result, str, str2);
    }
}
