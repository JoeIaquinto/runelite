/*
 * Copyright (c) 2018 kulers
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.inventorytags;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.inject.Inject;

import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.Query;
import net.runelite.api.queries.InventoryWidgetItemQuery;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.util.QueryRunner;

public class InventoryTagsOverlay extends Overlay
{
	private final QueryRunner queryRunner;
	private final ItemManager itemManager;
	private final InventoryTagsPlugin plugin;
	@Inject
	private Client client;
	@Inject
	private InventoryTagsOverlay(QueryRunner queryRunner, ItemManager itemManager, InventoryTagsPlugin plugin)
	{
		setPosition(OverlayPosition.DYNAMIC);
		setPriority(OverlayPriority.LOW);
		setLayer(OverlayLayer.ABOVE_WIDGETS);
		this.queryRunner = queryRunner;
		this.itemManager = itemManager;
		this.plugin = plugin;
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!plugin.isHasTaggedItems())
		{
			return null;
		}

		// Now query the inventory for the tagged item ids
		final Query query = new InventoryWidgetItemQuery();
		final WidgetItem[] widgetItems = queryRunner.runQuery(query);

		//Set current animation to conditionally highlight colors to aid 3t fish
		final int currentAnim = client.getLocalPlayer().getAnimation();
		// Iterate through all found items and draw the outlines
		for (final WidgetItem item : widgetItems)
		{
			final int itemId = item.getId();
			final String group = plugin.getTag(itemId);

			if (group != null)
			{

				Color color = Color.red;
				if ((itemId == 249 || itemId == 251 || itemId == 253 || itemId == 255 || itemId == 1939)){
					if (currentAnim == 623 || currentAnim == -1) color = Color.GREEN;
					if (currentAnim == 622) color = Color.YELLOW;
				}
				else if ((itemId == 11328 || itemId == 11330 || itemId == 11332) && currentAnim == 5249){
					color = Color.GREEN;
				}
				else {
					color = plugin.getGroupNameColor(group);
				}
				if (color != null)
				{

					final BufferedImage outline = itemManager.getItemOutline(item.getId(), item.getQuantity(), color);

					graphics.drawImage(outline, item.getCanvasLocation().getX() + 1, item.getCanvasLocation().getY() + 1, null);
				}
			}
		}

		return null;
	}
}
