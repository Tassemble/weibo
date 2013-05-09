package com.techq.weibo.dao;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import org.junit.Test;

/**
 * @author chq
 * 
 */
public class DAOLocker {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	static ConcurrentHashMap<Entry, Entry> map = new ConcurrentHashMap<Entry, Entry>();

	static final class Entry extends ReentrantLock {
		String owner = "";
		long corpId = -1l;
		long dirId = -1l;
		long ref = 0;

		public Entry(String owner, long corpId, long dirId) {
			this.owner = owner;
			this.corpId = corpId;
			this.dirId = dirId;
		}

		public Entry(long corpId, long dirId) {
			this.corpId = corpId;
			this.dirId = dirId;
		}

		public Entry(long newCorpId) {
			this.corpId = newCorpId;
		}

		public long getRef() {
			return ref;
		}

		public void setRef(long ref) {
			this.ref = ref;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + (int) (corpId ^ (corpId >>> 32));
			result = prime * result + (int) (dirId ^ (dirId >>> 32));
			result = prime * result + ((owner == null) ? 0 : owner.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Entry other = (Entry) obj;
			if (corpId != other.corpId)
				return false;
			if (dirId != other.dirId)
				return false;
			if (owner == null) {
				if (other.owner != null)
					return false;
			} else if (!owner.equals(other.owner))
				return false;
			return true;
		}

	}

	public void lock(String owner, long corpId, long dirId) {
		Entry entry = this.synchronizedAdd(owner, corpId, dirId);

		System.out.println(owner + " is wait...");
		entry.lock();
		System.out.println(owner + " has entered!");
	}

	public void unlock(String owner, long corpId, long dirId) {
		Entry entry = this.synchronizedMinus(owner, corpId, dirId);
		System.out.println(owner + " went out!");
		entry.unlock();
		entry = null;
	}

	public void qiyeLock(long corpId) {
		Entry entry = this.synchronizedAdd(corpId);
		entry.lock();
	}

	public void qiyeUnlock(long corpId) {
		Entry entry = this.synchronizedMinus(corpId);
		entry.unlock();
		entry = null;
	}

	public void lock(String lock) {
		this.lock(lock, 1, 1);
	}

	public void unLock(String lock) {
		this.unlock(lock, 1, 1);
	}

	private synchronized Entry synchronizedAdd(long corpId) {
		Entry entry = new Entry(corpId);
		if (map.containsKey(entry)) {
			entry = map.get(entry);
			long ref = entry.getRef();
			entry.setRef(ref + 1);
			System.out.println("ref add one, now is :" + entry.getRef());
			return entry;
		}
		entry.setRef(1);
		System.out.println("add new one");
		map.put(entry, entry);
		return entry;
	}

	private synchronized Entry synchronizedMinus(long corpId) {
		Entry entry = new Entry(corpId);
		entry = map.get(entry);
		if (entry.getRef() == 1)
			map.remove(entry);
		else {
			long ref = entry.getRef();
			entry.setRef(ref - 1);
		}
		return entry;
	}

	public synchronized Entry synchronizedAdd(String owner, long corpId,
			long dirId) {
		Entry entry = new Entry(owner, corpId, dirId);
		if (map.containsKey(entry)) {
			entry = map.get(entry);
			long ref = entry.getRef();
			entry.setRef(ref + 1);
			System.out.println(owner + " ref add one, now is :" + entry.getRef());
			return entry;
		}
		entry.setRef(1);
		System.out.println(owner + " add new one:" + owner);
		map.put(entry, entry);
		return entry;
	}

	public synchronized Entry synchronizedMinus(String owner, long corpId,
			long dirId) {
		Entry entry = new Entry(owner, corpId, dirId);
		entry = map.get(entry);
		if (entry.getRef() == 1) {
			map.remove(entry);
			System.out.println("remove :" + owner);
		} else {
			long ref = entry.getRef();
			entry.setRef(ref - 1);
			System.out.println(owner + " ref munis one, now is :" + entry.getRef());
		}
		return entry;
	}

	@Test
	public void testInsert() {
		final DAOLocker locker = new DAOLocker();
		ExecutorService exec = Executors.newCachedThreadPool();

		final List<String> owners = Arrays.asList("chq", "John", "chq", "John",
				"Kitty", "Kitty", "chq");
		for (int i = 0; i < owners.size(); i++) {
			final String owner = owners.get(i);
			exec.execute(new Runnable() {
				public void run() {
					try {
						locker.lock(owner);
						try {
							TimeUnit.SECONDS.sleep(10);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} finally {
						locker.unLock(owner);
					}
				}
			});
		}

		try {
			exec.shutdown();
			while (!exec.awaitTermination(1, TimeUnit.SECONDS)) {
				System.out.print(".");
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}