load("@rules_java//java:defs.bzl", "java_library")

java_library(
    name = "restapi",
    srcs = glob(["**/*.java"]),
    visibility = ["//visibility:public"],
    deps = [
        "//java/com/google/gerrit/auth",
        "//java/com/google/gerrit/common:annotations",
        "//java/com/google/gerrit/extensions:api",
        "//java/com/google/gerrit/server",
        "//lib/flogger:api",
        "//lib/guice",
    ],
)
