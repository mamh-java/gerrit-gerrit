load("//tools/bzl:plugin.bzl", "gerrit_plugin")

gerrit_plugin(
    name = "transsion",
    srcs = glob(["src/main/java/**/*.java"]),
    manifest_entries = [
        "Gerrit-PluginName: transsion",
        "Gerrit-Module: com.googlesource.gerrit.plugins.transsion.Module",
        "Gerrit-SshModule: com.googlesource.gerrit.plugins.transsion.SshModule",
    ],
    resources = glob(["src/main/resources/**/*"]),
    deps = [
        ":replication-neverlink",
        "//plugins/hooks",
    ],
)

java_library(
    name = "replication-neverlink",
    neverlink = 1,
    exports = ["//plugins/replication"],
)
