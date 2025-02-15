package com.dotcms.cli.command.files;

import static com.dotcms.cli.command.files.TreePrinter.COLOR_DELETED;
import static com.dotcms.cli.command.files.TreePrinter.COLOR_MODIFIED;
import static com.dotcms.cli.command.files.TreePrinter.COLOR_NEW;

import com.dotcms.api.client.files.PushService;
import com.dotcms.api.client.files.traversal.TraverseResult;
import com.dotcms.api.traversal.TreeNode;
import com.dotcms.api.traversal.TreeNodePushInfo;
import com.dotcms.cli.command.DotCommand;
import com.dotcms.cli.command.DotPush;
import com.dotcms.cli.common.ConsoleLoadingAnimation;
import com.dotcms.cli.common.OutputOptionMixin;
import com.dotcms.cli.common.PushMixin;
import com.dotcms.common.AssetsUtils;
import com.dotcms.common.LocalPathStructure;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import javax.enterprise.context.control.ActivateRequestContext;
import javax.inject.Inject;
import picocli.CommandLine;

@ActivateRequestContext
@CommandLine.Command(
        name = FilesPush.NAME,
        header = "@|bold,blue dotCMS Files push|@",
        description = {
                " This command push files to the server.",
                "" // empty string here so we can have a new line
        }
)
public class FilesPush extends AbstractFilesCommand implements Callable<Integer>,
        DotCommand, DotPush {

    static final String NAME = "push";
    static final String FILES_PUSH_MIXIN = "filesPushMixin";

    @CommandLine.Mixin
    PushMixin pushMixin;

    @CommandLine.Mixin(name = FILES_PUSH_MIXIN)
    FilesPushMixin filesPushMixin;

    @Inject
    PushService pushService;

    @CommandLine.Spec
    CommandLine.Model.CommandSpec spec;

    @Override
    public Integer call() throws Exception {

        // When calling from the global push we should avoid the validation of the unmatched
        // arguments as we may send arguments meant for other push subcommands
        if (!pushMixin.noValidateUnmatchedArguments) {
            // Checking for unmatched arguments
            output.throwIfUnmatchedArguments(spec.commandLine());
        }

        // Getting the workspace
        var workspace = getWorkspaceDirectory(pushMixin.path());

        CompletableFuture<List<TraverseResult>>
                folderTraversalFuture = CompletableFuture.supplyAsync(
                () ->
                    // Service to handle the traversal of the folder
                     pushService.traverseLocalFolders(output, workspace,
                            pushMixin.path().toFile(),
                            filesPushMixin.removeAssets, filesPushMixin.removeFolders,
                            false, true)
                );

        // ConsoleLoadingAnimation instance to handle the waiting "animation"
        ConsoleLoadingAnimation consoleLoadingAnimation = new ConsoleLoadingAnimation(
                output,
                folderTraversalFuture
        );

        CompletableFuture<Void> animationFuture = CompletableFuture.runAsync(
                consoleLoadingAnimation
        );

        // Waits for the completion of both the folder traversal and console loading animation tasks.
        // This line blocks the current thread until both CompletableFuture instances
        // (folderTraversalFuture and animationFuture) have completed.
        CompletableFuture.allOf(folderTraversalFuture, animationFuture).join();
        final var result = folderTraversalFuture.get();

        if (result == null) {
            output.error(String.format(
                    "Error occurred while pushing folder info: [%s].",
                    pushMixin.path().toAbsolutePath()));
            return CommandLine.ExitCode.SOFTWARE;
        }

        // Let's try to print these tree with some order
        result.sort((o1, o2) -> {
            var left = o1.localPaths();
            var right = o2.localPaths();
            return left.filePath().compareTo(right.filePath());
        });

        var count = 0;

        if (!result.isEmpty()) {
            for (var treeNodeData : result) {

                var localPaths = treeNodeData.localPaths();
                var treeNode = treeNodeData.treeNode();

                var outputBuilder = new StringBuilder();

                header(count++, localPaths, outputBuilder);

                var treeNodePushInfo = treeNode.collectPushInfo();

                if (treeNodePushInfo.hasChanges()) {

                    changesSummary(treeNodePushInfo, outputBuilder);

                    if (pushMixin.dryRun) {
                        dryRunSummary(localPaths, treeNode, outputBuilder);
                    }

                    output.info(outputBuilder.toString());

                    // ---
                    // Pushing the tree
                    if (!pushMixin.dryRun) {

                         pushService.processTreeNodes(output, workspace.getAbsolutePath(),
                                localPaths, treeNode, treeNodePushInfo, pushMixin.failFast,
                                pushMixin.retryAttempts);

                    }

                } else {
                    outputBuilder.
                            append("\r\n").
                            append(" ──────\n").
                            append(
                                    String.format(" No changes in %s to push%n%n", "Files"));
                    output.info(outputBuilder.toString());
                }
            }
        } else {
            output.info(String.format("\r%n"
                    + " ──────%n"
                    + " No changes in %s to push%n%n", "Files"));
        }

        return CommandLine.ExitCode.OK;
    }

    private void header(int count, LocalPathStructure localPaths,
            StringBuilder outputBuilder) {
        outputBuilder.append(count == 0 ? "\r\n" : "\n\n").
                append(" ──────\n").
                append(String.format(
                        " @|bold Folder [%s]|@ --- Site: [%s] - Status [%s] - Language [%s] %n",
                        localPaths.filePath(),
                        localPaths.site(),
                        localPaths.status(),
                        localPaths.language()));
    }

    private void dryRunSummary(LocalPathStructure localPaths, TreeNode treeNode,
            StringBuilder outputBuilder) {
        TreePrinter.getInstance().formatByStatus(
                outputBuilder,
                AssetsUtils.statusToBoolean(localPaths.status()),
                List.of(localPaths.language()),
                treeNode,
                false,
                true,
                localPaths.languageExists());
    }

    private void changesSummary(TreeNodePushInfo pushInfo, StringBuilder outputBuilder) {
        var assetsToPushCount = pushInfo.assetsToPushCount();
        if (assetsToPushCount > 0) {
            outputBuilder.append(String.format(" Push Data: " +
                            "@|bold [%s]|@ Assets to push: " +
                            "(@|bold,%s %s|@ New " +
                            "- @|bold,%s %s|@ Modified) " +
                            "- @|bold,%s [%s]|@ Assets to delete " +
                            "- @|bold,%s [%s]|@ Folders to push " +
                            "- @|bold,%s [%s]|@ Folders to delete\n\n",
                    COLOR_NEW,pushInfo.assetsToPushCount(),
                    COLOR_MODIFIED,pushInfo.assetsNewCount(),
                    COLOR_DELETED,pushInfo.assetsModifiedCount(),
                    COLOR_NEW,pushInfo.assetsToDeleteCount(),
                    COLOR_DELETED,pushInfo.foldersToPushCount(),
                    pushInfo.foldersToDeleteCount())
            );
        } else {
            outputBuilder.append(String.format(" Push Data: " +
                            "@|bold,%s [%s]|@ Assets to push " +
                            "- @|bold,%s [%s]|@ Assets to delete " +
                            "- @|bold,%s [%s]|@ Folders to push " +
                            "- @|bold,%s [%s]|@ Folders to delete\n\n",
                    COLOR_NEW,pushInfo.assetsToPushCount(),
                    COLOR_DELETED,pushInfo.assetsToDeleteCount(),
                    COLOR_NEW,pushInfo.foldersToPushCount(),
                    COLOR_DELETED,pushInfo.foldersToDeleteCount()));
        }
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public OutputOptionMixin getOutput() {
        return output;
    }

    @Override
    public PushMixin getPushMixin() {
        return pushMixin;
    }

    @Override
    public Optional<String> getCustomMixinName() {
        return Optional.of(FILES_PUSH_MIXIN);
    }

}
