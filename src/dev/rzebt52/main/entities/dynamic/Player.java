package dev.rzebt52.main.entities.dynamic;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.util.Iterator;

import dev.rzebt52.main.Conveyor;
import dev.rzebt52.main.entities.stock.Backpack;
import dev.rzebt52.main.graphics.Assets;
import dev.rzebt52.main.input.KeyHandler;
import dev.rzebt52.main.world.Region;

public class Player extends DynamicEntity {

	private boolean controlled;
	private Backpack backpack;

	public Player(int x, int y, boolean controlled, Conveyor conveyor) {

		super(x, y, conveyor);
		this.controlled = controlled;
		backpack = new Backpack(conveyor);

		speed = 5;

		bounds.setBounds(3 * Assets.DRAWRES, 4 * Assets.DRAWRES, 4 * Assets.DRAWRES, 4 * Assets.DRAWRES);

	}

	public void getControls() {

		KeyHandler keyHandler = conveyor.getKeyHandler();

		if (keyHandler.getKeys(KeyEvent.VK_W) && !keyHandler.getKeys(KeyEvent.VK_S)) {
			ySpeed = -speed;
		}

		else if (keyHandler.getKeys(KeyEvent.VK_S) && !keyHandler.getKeys(KeyEvent.VK_W)) {
			ySpeed = speed;
		}

		else {
			ySpeed = 0;
		}

		if (keyHandler.getKeys(KeyEvent.VK_A) && !keyHandler.getKeys(KeyEvent.VK_D)) {
			xSpeed = -speed;
		}

		else if (keyHandler.getKeys(KeyEvent.VK_D) && !keyHandler.getKeys(KeyEvent.VK_A)) {
			xSpeed = speed;
		}

		else {
			xSpeed = 0;
		}

	}

	@Override
	public void tick() {
		if (controlled) {
			backpack.tick();
			getControls();
			for (int x = 0; x < Region.REGIONUNLOADDISTANCE; x++) {
				for (int y = 0; y < Region.REGIONUNLOADDISTANCE; y++) {
					int regionX = (int) (this.x / Region.REGIONSIZE / Assets.DRAWSIZE + x
							- Region.REGIONUNLOADDISTANCE / 2);
					int regionY = (int) (this.y / Region.REGIONSIZE / Assets.DRAWSIZE + y
							- Region.REGIONUNLOADDISTANCE / 2);
					if (conveyor.getWorld().getRegion(regionX, regionY) == null) {
						conveyor.getWorld().loadRegion(regionX, regionY);
					}
				}
			}

			Iterator<Region> iterator = conveyor.getWorld().getRegions().iterator();
			while (iterator.hasNext()) {
				Region r = iterator.next();
				if (r.getWorldX() <= x / Region.REGIONSIZE / Assets.DRAWSIZE - Region.REGIONUNLOADDISTANCE / 2
						|| r.getWorldX() >= x / Region.REGIONSIZE / Assets.DRAWSIZE + Region.REGIONUNLOADDISTANCE / 2
						|| r.getWorldY() <= y / Region.REGIONSIZE / Assets.DRAWSIZE - Region.REGIONUNLOADDISTANCE / 2
						|| r.getWorldY() >= y / Region.REGIONSIZE / Assets.DRAWSIZE + Region.REGIONUNLOADDISTANCE / 2) {
					iterator.remove();
				}
			}
			if (conveyor.getMouseHandler().getPressed(3)) {
				int mouseX = conveyor.getMouseHandler().getMouseX();
				int mouseY = conveyor.getMouseHandler().getMouseY();
				conveyor.getWorld().setTile((mouseX + conveyor.getCamera().getxOffset()) / Assets.DRAWSIZE,
						(mouseY + conveyor.getCamera().getyOffset()) / Assets.DRAWSIZE, 1, 0);
			}
			if (conveyor.getMouseHandler().getPressed(1)) {
				int mouseX = conveyor.getMouseHandler().getMouseX();
				int mouseY = conveyor.getMouseHandler().getMouseY();
				if (conveyor.getWorld().getTile((mouseX + conveyor.getCamera().getxOffset()) / Assets.DRAWSIZE,
						(mouseY + conveyor.getCamera().getyOffset()) / Assets.DRAWSIZE, 1) == 0) {
					conveyor.getWorld().setTile((mouseX + conveyor.getCamera().getxOffset()) / Assets.DRAWSIZE,
							(mouseY + conveyor.getCamera().getyOffset()) / Assets.DRAWSIZE, 1, backpack.getCurrentTile().getId());
				}
			}
		}
		move();
	}

	@Override
	public void render(Graphics g) {
		backpack.render(g);
		g.drawImage(Assets.player, (int) (x - conveyor.getCamera().getxOffset()),
				(int) (y - conveyor.getCamera().getyOffset()), width, height, null);
		// g.setColor(Color.RED);
		// g.drawRect((int) (bounds.x + x), (int) (y + bounds.y), bounds.width,
		// bounds.height);

	}

}
