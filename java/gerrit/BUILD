load("@rules_java//java:defs.bzl", "java_library")

java_library(
    name = "prolog-predicates",
    srcs = glob(["**/*.java"]),
    visibility = ["//visibility:public"],
    deps = [
        "//java/com/google/gerrit/common:annotations",
        "//java/com/google/gerrit/entities",
        "//java/com/google/gerrit/extensions:api",
        "//java/com/google/gerrit/server",
        "//java/com/google/gerrit/server/rules/prolog",
        "//lib:jgit",
        "//lib/flogger:api",
        "//lib/prolog:runtime",
        "@guava//jar",
    ],
)
