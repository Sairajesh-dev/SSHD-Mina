package com.example;

import org.apache.sshd.common.file.root.RootedFileSystemProvider;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@Component
public class InMemoryFileSystemProvider extends RootedFileSystemProvider {
	private final Path root;
	private final InMemoryFileSystem fileSystem;

	@Autowired
	public InMemoryFileSystemProvider(@Qualifier("rootPath") Path root, InMemoryFileSystem fileSystem) {
		this.root = root;
		this.fileSystem = fileSystem;
	}

	@Override
	public void checkAccess(Path path, AccessMode... modes) throws IOException {
		String virtualPath = path.toString().replace(root.toString(), "");
		if (!fileSystem.exists(virtualPath)) {
			throw new NoSuchFileException(virtualPath);
		}
	}

	@Override
	public <A extends BasicFileAttributes> A readAttributes(Path path, Class<A> type, LinkOption... options)
			throws IOException {
		String virtualPath = path.toString().replace(root.toString(), "");
		FileEntry entry = fileSystem.getEntry(virtualPath);

		BasicFileAttributes attrs = new BasicFileAttributesImpl(entry);
		return type.cast(attrs);
	}

	@Override
	public SeekableByteChannel newByteChannel(Path path, Set<? extends OpenOption> options, FileAttribute<?>... attrs)
			throws IOException {
		String virtualPath = normalizePath(path);

		if (!fileSystem.exists(virtualPath)) {
			throw new NoSuchFileException(virtualPath);
		}

		FileEntry entry = fileSystem.getEntry(virtualPath);
		if (entry == null || entry.isDirectory()) {
			throw new NoSuchFileException(virtualPath);
		}

		return new SeekableByteChannel() {
			private final byte[] content = entry.getContent() != null ? entry.getContent() : new byte[0];
			private int position = 0;

			@Override
			public int read(ByteBuffer dst) throws IOException {
				if (position >= content.length)
					return -1;
				int remaining = content.length - position;
				int toRead = Math.min(remaining, dst.remaining());
				dst.put(content, position, toRead);
				position += toRead;
				return toRead;
			}

			@Override
			public int write(ByteBuffer src) throws IOException {
				byte[] newContent = new byte[src.remaining()];
				src.get(newContent);
				fileSystem.createFile(virtualPath, newContent);
				position += newContent.length;
				return newContent.length;
			}

			@Override
			public long position() {
				return position;
			}

			@Override
			public SeekableByteChannel position(long newPosition) {
				this.position = (int) newPosition;
				return this;
			}

			@Override
			public long size() {
				return content.length;
			}

			@Override
			public SeekableByteChannel truncate(long size) {
				return this;
			}

			@Override
			public boolean isOpen() {
				return true;
			}

			@Override
			public void close() {
			}
		};
	}

	private String normalizePath(Path path) {
		String virtualPath = path.toString().replace(root.toString(), "").replace("\\", "/");
		if (!virtualPath.startsWith("/")) {
			virtualPath = "/" + virtualPath;
		}
		return virtualPath;
	}

	@Override
	public DirectoryStream<Path> newDirectoryStream(Path dir, DirectoryStream.Filter<? super Path> filter)
			throws IOException {
		String virtualPath = dir.toString().replace(root.toString(), "");
		if (virtualPath.isEmpty())
			virtualPath = "/";

		final String dirPath = virtualPath;

		return new DirectoryStream<Path>() {
			@Override
			public Iterator<Path> iterator() {
				List<Path> contents = new ArrayList<>();
				fileSystem.getDirectoryContents(dirPath).forEach(entry -> contents.add(dir.resolve(entry)));
				return contents.iterator();
			}

			@Override
			public void close() {
			}
		};
	}

	@Override
	public void createDirectory(Path dir, FileAttribute<?>... attrs) throws IOException {
		String virtualPath = dir.toString().replace(root.toString(), "");
		fileSystem.createDirectory(virtualPath);
	}
}